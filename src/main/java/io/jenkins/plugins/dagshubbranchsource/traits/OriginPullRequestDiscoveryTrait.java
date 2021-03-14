package io.jenkins.plugins.dagshubbranchsource.traits;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import io.jenkins.plugins.dagshubbranchsource.DAGsHubSCMSource;
import io.jenkins.plugins.dagshubbranchsource.DAGsHubSCMSourceContext;
import jenkins.scm.api.SCMHeadCategory;
import jenkins.scm.api.SCMHeadOrigin;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.mixin.ChangeRequestCheckoutStrategy;
import jenkins.scm.api.mixin.ChangeRequestSCMHead2;
import jenkins.scm.api.trait.SCMHeadAuthority;
import jenkins.scm.api.trait.SCMHeadAuthorityDescriptor;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceRequest;
import jenkins.scm.api.trait.SCMSourceTrait;
import jenkins.scm.api.trait.SCMSourceTraitDescriptor;
import jenkins.scm.impl.ChangeRequestSCMHeadCategory;
import jenkins.scm.impl.trait.Discovery;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * A {@link Discovery} trait for DAGsHub that will discover PRs originating from a branch
 * in the repository itself.
 */
public class OriginPullRequestDiscoveryTrait extends SCMSourceTrait {

    /**
     * If true, run the build on the tip of the PR head, instead of creating a merge commit first
     */
    private boolean buildOnPullHead;

    public OriginPullRequestDiscoveryTrait() {
        this(false);
    }

    @DataBoundConstructor
    public OriginPullRequestDiscoveryTrait(boolean buildOnPullHead) {
        this.buildOnPullHead = buildOnPullHead;
    }

    public boolean isBuildOnPullHead() {
        return buildOnPullHead;
    }

    @DataBoundSetter
    public void setBuildOnPullHead(boolean buildOnPullHead) {
        this.buildOnPullHead = buildOnPullHead;
    }

    @NonNull
    public ChangeRequestCheckoutStrategy getStrategy() {
        return isBuildOnPullHead() ?
            ChangeRequestCheckoutStrategy.HEAD
            : ChangeRequestCheckoutStrategy.MERGE;
    }

    @Override
    protected void decorateContext(SCMSourceContext<?, ?> context) {
        DAGsHubSCMSourceContext ctx = (DAGsHubSCMSourceContext) context;
        ctx.setWantOriginPullRequests(true);
        ctx.withAuthority(new OriginChangeRequestSCMHeadAuthority());
        ctx.setOriginPullStrategy(getStrategy());
    }

    @Override
    public boolean includeCategory(@NonNull SCMHeadCategory category) {
        return category instanceof ChangeRequestSCMHeadCategory;
    }

    @Extension
    @Discovery
    public static class DescriptorImpl extends SCMSourceTraitDescriptor {

        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.OriginPullRequestDiscoveryTrait_displayName();
        }

        @Override
        public Class<? extends SCMSourceContext> getContextClass() {
            return DAGsHubSCMSourceContext.class;
        }

        @Override
        public Class<? extends SCMSource> getSourceClass() {
            return DAGsHubSCMSource.class;
        }
    }

    /**
     * A {@link SCMHeadAuthority} that trusts origin pull requests
     */
    public static class OriginChangeRequestSCMHeadAuthority
        extends SCMHeadAuthority<SCMSourceRequest, ChangeRequestSCMHead2, SCMRevision> {

        @Override
        protected boolean checkTrusted(@NonNull SCMSourceRequest request,
            @NonNull ChangeRequestSCMHead2 head) {
            return SCMHeadOrigin.DEFAULT.equals(head.getOrigin());
        }

        @Extension
        public static class DescriptorImpl extends SCMHeadAuthorityDescriptor {

            @Override
            public boolean isApplicableToOrigin(
                @NonNull Class<? extends SCMHeadOrigin> originClass) {
                return SCMHeadOrigin.Default.class.isAssignableFrom(originClass);
            }

            @NonNull
            @Override
            public String getDisplayName() {
                return Messages.OriginPullRequestDiscoveryTrait_authorityDisplayName();
            }
        }
    }
}
