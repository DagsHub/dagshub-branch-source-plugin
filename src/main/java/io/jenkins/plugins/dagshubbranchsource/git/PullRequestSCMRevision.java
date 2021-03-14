package io.jenkins.plugins.dagshubbranchsource.git;

import edu.umd.cs.findbugs.annotations.NonNull;
import jenkins.plugins.git.GitBranchSCMRevision;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.mixin.ChangeRequestSCMRevision;
import org.kohsuke.stapler.export.Exported;

public class PullRequestSCMRevision extends ChangeRequestSCMRevision<PullRequestSCMHead> {

    @NonNull
    private final PullRequestSCMHead head;
    private final @NonNull String baseHash;
    private final @NonNull String headHash;
    private final GitBranchSCMRevision origin;

    /**
     * @param head the {@link PullRequestSCMHead} that the {@link SCMRevision} belongs to.
     * @param target the {@link GitBranchSCMRevision} of the {@link PullRequestSCMHead#getTarget()}.
     * @param origin the {@link GitBranchSCMRevision} of the {@link PullRequestSCMHead#getOrigin()}
     * head.
     */
    public PullRequestSCMRevision(
        @NonNull PullRequestSCMHead head,
        @NonNull GitBranchSCMRevision target,
        @NonNull GitBranchSCMRevision origin) {
        super(head, target);
        this.head = head;
        this.baseHash = target.getHash();
        this.headHash = origin.getHash();
        this.origin = origin;
    }

    @NonNull
    public String getBaseHash() {
        return baseHash;
    }

    @NonNull
    public String getHeadHash() {
        return headHash;
    }

    @Exported
    @NonNull
    public final GitBranchSCMRevision getOrigin() {
        return origin;
    }

    @NonNull
    public PullRequestSCMHead getPullHead() {
        return head;
    }

    @Override
    public boolean equivalent(ChangeRequestSCMRevision<?> revision) {
        return (revision instanceof PullRequestSCMRevision)
            && origin.equals(((PullRequestSCMRevision) revision).getOrigin());
    }

    @Override
    protected int _hashCode() {
        return origin.hashCode();
    }

    @Override
    public String toString() {
        return (isMerge() ? ((GitBranchSCMRevision) getTarget()).getHash() + "+" : "") + origin.getHash();
    }
}
