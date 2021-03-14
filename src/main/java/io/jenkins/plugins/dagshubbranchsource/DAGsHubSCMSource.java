package io.jenkins.plugins.dagshubbranchsource;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.Queue.Task;
import hudson.model.TaskListener;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.dagshubbranchsource.api.Branch;
import io.jenkins.plugins.dagshubbranchsource.api.DAGsHubApi;
import io.jenkins.plugins.dagshubbranchsource.api.PullRequest;
import io.jenkins.plugins.dagshubbranchsource.api.Tag;
import io.jenkins.plugins.dagshubbranchsource.git.PullRequestSCMHead;
import io.jenkins.plugins.dagshubbranchsource.git.PullRequestSCMRevision;
import io.jenkins.plugins.dagshubbranchsource.traits.BranchDiscoveryTrait;
import io.jenkins.plugins.dagshubbranchsource.traits.OriginPullRequestDiscoveryTrait;
import io.jenkins.plugins.dagshubbranchsource.traits.TagDiscoveryTrait;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jenkins.model.Jenkins;
import jenkins.plugins.git.AbstractGitSCMSource;
import jenkins.plugins.git.GitBranchSCMHead;
import jenkins.plugins.git.GitBranchSCMRevision;
import jenkins.plugins.git.GitSCMBuilder;
import jenkins.plugins.git.GitTagSCMHead;
import jenkins.plugins.git.GitTagSCMRevision;
import jenkins.plugins.git.MergeWithGitSCMExtension;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMHeadCategory;
import jenkins.scm.api.SCMHeadEvent;
import jenkins.scm.api.SCMHeadObserver;
import jenkins.scm.api.SCMHeadOrigin;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMSourceCriteria;
import jenkins.scm.api.SCMSourceDescriptor;
import jenkins.scm.api.SCMSourceOwner;
import jenkins.scm.api.trait.SCMSourceTrait;
import jenkins.scm.impl.ChangeRequestSCMHeadCategory;
import jenkins.scm.impl.TagSCMHeadCategory;
import jenkins.scm.impl.UncategorizedSCMHeadCategory;
import org.acegisecurity.Authentication;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

public class DAGsHubSCMSource extends AbstractGitSCMSource {

    private String repositoryUrl;
    private String credentialsId;
    private List<SCMSourceTrait> traits;

    @DataBoundConstructor
    public DAGsHubSCMSource(String repositoryUrl, String credentialsId, List<SCMSourceTrait> traits) {
        this.repositoryUrl = repositoryUrl;
        this.credentialsId = credentialsId;
        this.traits = Collections.unmodifiableList(traits);
    }

    @Override
    public String getCredentialsId() {
        return credentialsId;
    }

    @DataBoundSetter
    public void setCredentialsId(String credentialsId) {
        this.credentialsId = credentialsId;
    }

    @Override
    public String getRemote() {
        return getRepositoryUrl();
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    @DataBoundSetter
    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    @NonNull
    @Override
    public List<SCMSourceTrait> getTraits() {
        return traits;
    }

    @Override
    @DataBoundSetter
    public void setTraits(List<SCMSourceTrait> traits) {
        this.traits = Collections.unmodifiableList(new ArrayList<>(traits));
    }

    @NonNull
    @Override
    public SCMRevision getTrustedRevision(@NonNull SCMRevision revision, @NonNull TaskListener listener) {
        if (revision instanceof PullRequestSCMRevision) {
            PullRequestSCMHead head = (PullRequestSCMHead) revision.getHead();

            // If the head branch of the PR also came from the repo itself and not a fork, then
            // its author has write access to the repo anyway and they can be trusted
            if (head.getOrigin().equals(SCMHeadOrigin.DEFAULT)) {
                return revision;
            }

            // TODO: Here, we can check if the head version from a fork is also trusted -
            // by e.g. checking if its author has write access to the repo

            PullRequestSCMRevision rev = (PullRequestSCMRevision) revision;
            listener.getLogger()
                .format("Loading trusted files from target branch %s at %s rather than %s %s%n",
                    head.getTarget().getName(), rev.getBaseHash(), rev.getHead().getName(), rev.getHeadHash());
            return new SCMRevisionImpl(head.getTarget(), rev.getBaseHash());
        }
        return revision;
    }

    @Override
    public void afterSave() {
        // TODO: Register a webhook on the DAGsHub server, if possible given credentials
    }

    @Override
    protected GitSCMBuilder<?> newBuilder(@NonNull SCMHead head, SCMRevision revision) {
        final GitSCMBuilder<?> builder = super.newBuilder(head, revision);
        // To support pull requests, we need to give specific refspecs to the builder - pull requests
        // have non-standard refspecs
        builder.withoutRefSpecs();
        if (head instanceof PullRequestSCMHead) {
            PullRequestSCMHead h = (PullRequestSCMHead) head;
            PullRequestSCMRevision r = (PullRequestSCMRevision) revision;
            builder.withRefSpec(pullRefSpec(h));
            builder.withRefSpec(branchRefSpec(h.getTarget().getName()));

            if (r != null && r.isMerge()) {
                // This extension should instruct Jenkins to first merge the target (base) of the PR
                // into the head of the PR before trying to build.
                // To be extra correct, we should be providing a new implementation of SCMBuilder which
                // adds this extension as the last possible extension before building the SCM, since this
                // extension can be overridden by various git clone extensions.
                builder.withExtension(new MergeWithGitSCMExtension(
                    r.getTarget().getHead().getName(), r.getBaseHash()
                ));
            }

        } else if (head instanceof GitBranchSCMHead) {
            builder.withRefSpec(branchRefSpec(head.getName()));
        } else if (head instanceof GitTagSCMHead) {
            // Ordinarily, this wouldn't be required as git tags get copied regardless of refspecs.
            // But, the default behavior of GitSCMBuilder is that it might explicitly clone without tags
            // So this is to make sure our targeted tag arrives safely.
            builder.withRefSpec(tagRefSpec(head.getName()));
        }
        return builder;
    }

    public static String pullRefSpec(PullRequestSCMHead h) {
        return "+refs/pull/" + h.getNumber() + "/head:refs/remotes/@{remote}/" + h.getName();
    }

    public static String branchRefSpec(String name) {
        return "+refs/heads/" + name + ":refs/remotes/@{remote}/" + name;
    }

    public static String tagRefSpec(String name) {
        return "+refs/tags/" + name + ":refs/tags/" + name;
    }

    @Override
    protected SCMRevision retrieve(@NonNull SCMHead head, @NonNull TaskListener listener)
        throws IOException, InterruptedException {
        try (final DAGsHubApi api = createApi()){
            if (head instanceof GitBranchSCMHead) {
                listener.getLogger().format("Querying the current revision of branch %s...%n", head.getName());
                GitBranchSCMRevision rev = api.getBranch(head.getName()).toRev();
                listener.getLogger().format("Current revision of branch %s is %s%n", head.getName(), rev);
                return rev;
            }
            if (head instanceof GitTagSCMHead) {
                listener.getLogger().format("Querying the current revision of tag %s...%n", head.getName());
                GitTagSCMRevision rev = api.getTag(head.getName()).toRev();
                listener.getLogger().format("Current revision of tag %s is %s%n", head.getName(), rev);
                return rev;
            }
            if (head instanceof PullRequestSCMHead) {
                listener.getLogger().format("Querying the current revision of pull request %s...%n", head.getName());
                final PullRequestSCMRevision rev = api.getPull(((PullRequestSCMHead) head).getNumber())
                    .toRev(((PullRequestSCMHead) head).getCheckoutStrategy());
                listener.getLogger().format("Current revision of pull request %s is %s%n", head.getName(), rev);
                return rev;
            }
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        return null;
    }

    @Override
    protected void retrieve(SCMSourceCriteria criteria, @NonNull SCMHeadObserver observer,
        SCMHeadEvent<?> event, @NonNull TaskListener listener)
        throws IOException, InterruptedException {
        try (final DAGsHubSCMSourceRequest request = createRequest(observer, listener);
            final DAGsHubApi api = createApi()) {

            if (request.isFetchBranches()) {
                listener.getLogger().println("Listing branches");
                final List<GitBranchSCMRevision> branches =
                    api.getBranches().stream().map(Branch::toRev).collect(Collectors.toList());
                listener.getLogger().format("Found %d branches total%n", branches.size());
                for (GitBranchSCMRevision rev : branches) {
                    // TODO: Use request.process after we implement an SCMProbe or SCMFileSystem
                    final SCMHead head = rev.getHead();
                    if (request.isExcluded(head)) {
                        listener.getLogger().format("Branch %s is excluded, skipping%n", head.getName());
                        continue;
                    }
                    if (!observer.isObserving()) {
                        return;
                    }
                    listener.getLogger().format("Processing branch %s%n", head.getName());
                    observer.observe(head, rev);
                }
            }

            if (request.isFetchTags()) {
                listener.getLogger().println("Listing tags");
                final List<GitTagSCMRevision> tags =
                    api.getTags().stream().map(Tag::toRev).collect(Collectors.toList());
                listener.getLogger().format("Found %d tags total%n", tags.size());
                for (GitTagSCMRevision rev : tags) {
                    final SCMHead head = rev.getHead();
                    if (request.isExcluded(head)) {
                        listener.getLogger().format("Tag %s is excluded, skipping%n", head.getName());
                        continue;
                    }
                    if (!observer.isObserving()) {
                        return;
                    }
                    listener.getLogger().format("Processing tag %s%n", head.getName());
                    observer.observe(head, rev);
                }
            }

            if (request.isFetchAnyPullRequests()) {
                Stream<PullRequestSCMRevision> pullsStream = Stream.empty();
                final List<PullRequest> pulls = api.getPulls();
                if (request.isFetchOriginPullRequests()) {
                    listener.getLogger().println("Listing pull requests from one branch in the repo to another branch");
                    pullsStream = pulls.stream()
                        .filter(PullRequest::isSameOrigin)
                        .map(pr -> pr.toRev(request.getOriginPullStrategy()));
                }
                if (request.isFetchForkPullRequests()) {
                    listener.getLogger().println("Listing pull requests from forks to the target repo");
                    pullsStream = Stream.concat(pullsStream, pulls.stream()
                        .filter(pr -> !pr.isSameOrigin())
                        .map(pr -> pr.toRev(request.getForkPullStrategy())));
                }
                final List<PullRequestSCMRevision> pullRevs = pullsStream.collect(Collectors.toList());
                listener.getLogger().format("Found %d pull requests%n", pullRevs.size());
                for (PullRequestSCMRevision rev : pullRevs) {
                    final PullRequestSCMHead head = rev.getPullHead();
                    if (request.isExcluded(head)) {
                        listener.getLogger().format("Pull request %s is excluded, skipping", head.getId());
                        continue;
                    }
                    if (!observer.isObserving()) {
                        return;
                    }
                    listener.getLogger().format("Processing pull request %s%n", head.getId());
                    observer.observe(head, rev);
                }
            }
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    private DAGsHubApi createApi() throws URISyntaxException {
        return DAGsHubApi.create(getRepositoryUrl(), getCredentials());
    }

    private DAGsHubSCMSourceRequest createRequest(SCMHeadObserver observer, TaskListener listener) {
        return new DAGsHubSCMSourceContext(this.getCriteria(), observer)
            .withTraits(getTraits())
            .newRequest(this, listener);
    }

    @Override
    protected SCMRevision retrieve(@NonNull String revision, @NonNull TaskListener listener, Item retrieveContext)
        throws IOException, InterruptedException {
        SCMHeadObserver.Named observer = SCMHeadObserver.named(revision);
        retrieve(null, observer, null, listener);
        return observer.result();
    }

    @NonNull
    @Override
    protected Set<String> retrieveRevisions(@NonNull TaskListener listener, Item retrieveContext)
        throws IOException, InterruptedException {
        // don't pass through to AbstractGitSCMSource, instead use the SCMSource behaviour
        Set<String> revisions = new HashSet<>();
        for (SCMHead head : retrieve(listener)) {
            revisions.add(head.getName());
        }
        return revisions;
    }

    @Symbol("dagshubScmSource")
    @Extension
    public static class DescriptorImpl extends SCMSourceDescriptor {

        @NonNull
        public String getDisplayName() {
            return Messages.DAGsHubSCMSource_DisplayName();
        }

        @Override
        public String getPronoun() {
            return Messages.DAGsHubSCMSource_Pronoun();
        }

        /**
         * In charge of populating the list of possible credentials to use for the given repository URL.
         */
        @SuppressWarnings("unused") // Used by jelly UI
        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath SCMSourceOwner context,
            @QueryParameter String repositoryUrl, @QueryParameter String credentialsId) {
            StandardListBoxModel result = new StandardListBoxModel();
            if (context == null) {
                // must have admin if you want the list without a context
                if (!Jenkins.get().hasPermission(Jenkins.ADMINISTER)) {
                    return result.includeCurrentValue(credentialsId);
                }
            } else {
                if (!context.hasPermission(Item.EXTENDED_READ)
                    && !context.hasPermission(CredentialsProvider.USE_ITEM)) {
                    // must be able to read the configuration or use the item credentials if you
                    // want the list
                    return result.includeCurrentValue(credentialsId);
                }
            }

            Authentication authentication =
                context instanceof Task ? ((Task) context).getDefaultAuthentication() : ACL.SYSTEM;

            return result
                .includeEmptyValue()
                .includeAs(authentication, context, StandardUsernamePasswordCredentials.class,
                    URIRequirementBuilder.fromUri(repositoryUrl).build())
                .includeAs(authentication, context, StandardUsernameCredentials.class,
                    URIRequirementBuilder.fromUri(repositoryUrl).build())
                .includeCurrentValue(credentialsId);
        }

        @NonNull
        @Override
        public List<SCMSourceTrait> getTraitsDefaults() {
            return Arrays.asList(
                new BranchDiscoveryTrait()
                , new TagDiscoveryTrait()
                , new OriginPullRequestDiscoveryTrait()
//                , new ForkPullRequestDiscoveryTrait()
            );
        }

        @NonNull
        @Override
        protected SCMHeadCategory[] createCategories() {
            return new SCMHeadCategory[]{
                UncategorizedSCMHeadCategory.DEFAULT,
                TagSCMHeadCategory.DEFAULT,
                new ChangeRequestSCMHeadCategory(Messages._DAGsHubSCMSource_ChangeRequestCategory()),
            };
        }
    }
}
