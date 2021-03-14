package io.jenkins.plugins.dagshubbranchsource.traits;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import io.jenkins.plugins.dagshubbranchsource.DAGsHubSCMSource;
import io.jenkins.plugins.dagshubbranchsource.DAGsHubSCMSourceContext;
import jenkins.plugins.git.GitBranchSCMHead;
import jenkins.plugins.git.GitBranchSCMRevision;
import jenkins.scm.api.SCMHeadCategory;
import jenkins.scm.api.SCMHeadOrigin;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.trait.SCMHeadAuthority;
import jenkins.scm.api.trait.SCMHeadAuthorityDescriptor;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceRequest;
import jenkins.scm.api.trait.SCMSourceTrait;
import jenkins.scm.api.trait.SCMSourceTraitDescriptor;
import jenkins.scm.impl.trait.Discovery;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * A {@link Discovery} trait for DAGsHub that will discover branches on the repository.
 */
public class BranchDiscoveryTrait extends SCMSourceTrait {

    @DataBoundConstructor
    public BranchDiscoveryTrait() {
    }

    @Override
    protected void decorateContext(SCMSourceContext<?, ?> context) {
        ((DAGsHubSCMSourceContext) context).setWantBranches(true);
        context.withAuthority(new BranchSCMHeadAuthority());
    }

    @Override
    public boolean includeCategory(@NonNull SCMHeadCategory category) {
        return category.isUncategorized();
    }

    @Symbol("dagshubBranchDiscovery")
    @Extension
    @Discovery
    public static class DescriptorImpl extends SCMSourceTraitDescriptor {

        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.BranchDiscoveryTrait_displayName();
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
     * Trusts branches from the origin project.
     */
    public static class BranchSCMHeadAuthority extends
        SCMHeadAuthority<SCMSourceRequest, GitBranchSCMHead, GitBranchSCMRevision> {

        @Override
        protected boolean checkTrusted(@NonNull SCMSourceRequest request, @NonNull GitBranchSCMHead head) {
            return true;
        }

        @Extension
        public static class DescriptorImpl extends SCMHeadAuthorityDescriptor {

            @NonNull
            @Override
            public String getDisplayName() {
                return Messages.BranchDiscoveryTrait_authorityDisplayName();
            }

            @Override
            public boolean isApplicableToOrigin(@NonNull Class<? extends SCMHeadOrigin> originClass) {
                return SCMHeadOrigin.Default.class.isAssignableFrom(originClass);
            }
        }
    }
}

