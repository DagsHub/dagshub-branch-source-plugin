package io.jenkins.plugins.dagshubbranchsource.git;

import edu.umd.cs.findbugs.annotations.NonNull;
import jenkins.plugins.git.GitBranchSCMHead;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMHeadOrigin;
import jenkins.scm.api.mixin.ChangeRequestCheckoutStrategy;
import jenkins.scm.api.mixin.ChangeRequestSCMHead2;

public class PullRequestSCMHead extends SCMHead implements ChangeRequestSCMHead2 {

    private final long id;
    private final long number;
    private final GitBranchSCMHead target;
    private final ChangeRequestCheckoutStrategy strategy;
    private final String originName;
    private final String originOwner;
    private final SCMHeadOrigin origin;
    private final String originProjectPath;
    private final String title;

    /**
     * Constructor.
     *  @param name the name of the PR (Probably PR-XX).
     * @param id the pull request id.
     * @param number the index of the PR inside the origin repo (counts both issues and PRs)
     * @param target the target of this merge request.
     * @param strategy the checkout strategy
     * @param origin the origin of the merge request
     * @param originOwner the name of the owner of the origin project
     * @param originProjectPath the name of the origin project path
     * @param originName the name of the branch in the origin project
     * @param title the title of the merge request
     */
    public PullRequestSCMHead(@NonNull String name, long id, long number, GitBranchSCMHead target,
        ChangeRequestCheckoutStrategy strategy, SCMHeadOrigin origin, String originOwner,
        String originProjectPath, String originName, String title) {
        super(name);
        this.id = id;
        this.number = number;
        this.target = target;
        this.strategy = strategy;
        this.origin = origin;
        this.originOwner = originOwner;
        this.originProjectPath = originProjectPath;
        this.originName = originName;
        this.title = title;
    }

    @Override
    public String getPronoun() {
        return Messages.PullRequestSCMHead_Pronoun();
    }

    @NonNull
    @Override
    public ChangeRequestCheckoutStrategy getCheckoutStrategy() {
        return strategy;
    }

    @NonNull
    @Override
    public String getOriginName() {
        return originName;
    }

    @NonNull
    @Override
    public String getId() {
        return Long.toString(id);
    }

    public long getNumber() {
        return number;
    }

    @NonNull
    @Override
    public GitBranchSCMHead getTarget() {
        return target;
    }

    @NonNull
    @Override
    public SCMHeadOrigin getOrigin() {
        return origin;
    }

    public String getOriginOwner() {
        return originOwner;
    }

    public String getOriginProjectPath() {
        return originProjectPath;
    }

    public String getTitle() {
        return title;
    }
}
