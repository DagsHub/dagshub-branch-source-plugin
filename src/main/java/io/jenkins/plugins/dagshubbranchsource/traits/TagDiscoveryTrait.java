package io.jenkins.plugins.dagshubbranchsource.traits;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import io.jenkins.plugins.dagshubbranchsource.DAGsHubSCMSource;
import io.jenkins.plugins.dagshubbranchsource.DAGsHubSCMSourceContext;
import jenkins.plugins.git.GitTagSCMHead;
import jenkins.plugins.git.GitTagSCMRevision;
import jenkins.scm.api.SCMHeadCategory;
import jenkins.scm.api.SCMHeadOrigin;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.trait.SCMHeadAuthority;
import jenkins.scm.api.trait.SCMHeadAuthorityDescriptor;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceRequest;
import jenkins.scm.api.trait.SCMSourceTrait;
import jenkins.scm.api.trait.SCMSourceTraitDescriptor;
import jenkins.scm.impl.TagSCMHeadCategory;
import jenkins.scm.impl.trait.Discovery;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * A {@link Discovery} trait for DAGsHub that will discover tags on the repo.
 */
public class TagDiscoveryTrait extends SCMSourceTrait {

    @DataBoundConstructor
    public TagDiscoveryTrait() {
    }

    @Override
    protected void decorateContext(SCMSourceContext<?, ?> context) {
        DAGsHubSCMSourceContext ctx = (DAGsHubSCMSourceContext) context;
        ctx.setWantTags(true);
        ctx.withAuthority(new TagSCMHeadAuthority());
    }

    @Override
    public boolean includeCategory(@NonNull SCMHeadCategory category) {
        return category instanceof TagSCMHeadCategory;
    }

    @Symbol("dagshubTagDiscovery")
    @Extension
    @Discovery
    public static class DescriptorImpl extends SCMSourceTraitDescriptor {

        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.TagDiscoveryTrait_displayName();
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
     * Trusts tags from the origin project.
     */
    public static class TagSCMHeadAuthority extends
        SCMHeadAuthority<SCMSourceRequest, GitTagSCMHead, GitTagSCMRevision> {

        @Override
        protected boolean checkTrusted(@NonNull SCMSourceRequest request, @NonNull GitTagSCMHead head) {
            return true;
        }

        @Extension
        public static class DescriptorImpl extends SCMHeadAuthorityDescriptor {

            @NonNull
            @Override
            public String getDisplayName() {
                return Messages.TagDiscoveryTrait_authorityDisplayName();
            }

            @Override
            public boolean isApplicableToOrigin(@NonNull Class<? extends SCMHeadOrigin> originClass) {
                return SCMHeadOrigin.Default.class.isAssignableFrom(originClass);
            }
        }
    }
}

