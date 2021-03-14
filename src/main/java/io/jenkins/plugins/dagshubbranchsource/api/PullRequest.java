package io.jenkins.plugins.dagshubbranchsource.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.jenkins.plugins.dagshubbranchsource.git.PullRequestSCMHead;
import io.jenkins.plugins.dagshubbranchsource.git.PullRequestSCMRevision;
import java.time.ZonedDateTime;
import java.util.Locale;
import jenkins.plugins.git.GitBranchSCMHead;
import jenkins.plugins.git.GitBranchSCMRevision;
import jenkins.scm.api.SCMHeadOrigin;
import jenkins.scm.api.SCMHeadOrigin.Fork;
import jenkins.scm.api.mixin.ChangeRequestCheckoutStrategy;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PullRequest {

    private long id;
    private long number;
    private User user;
    private String title;
    private String body;
    /*
    Labels    []*Label   `json:"labels"`
	Milestone *Milestone `json:"milestone"`
     */

    private User assignee;
    private State state;
    private int comments;

    private String headBranch;
    private Commit headCommit;
    private Repository headRepo;
    private String baseBranch;
    private Commit baseCommit;
    private Repository baseRepo;
    private boolean sameOrigin;

    private String htmlUrl;

    private Boolean mergeable;
    private boolean hasMerged;
    private ZonedDateTime mergedAt;
    private String mergedCommitSha;
    private User mergedBy;

    public PullRequestSCMRevision toRev(ChangeRequestCheckoutStrategy strategy) {
        final GitBranchSCMHead targetHead = new GitBranchSCMHead(getBaseBranch());
        return new PullRequestSCMRevision(
            new PullRequestSCMHead(
                "PR-" + getNumber() + "-" + strategy.name(),
                getId(),
                getNumber(),
                targetHead,
                strategy,
                sameOrigin ? SCMHeadOrigin.DEFAULT : new Fork(getHeadRepo().getFullName()),
                getHeadRepo().getOwner().getUserName(),
                getBaseRepo().getHtmlUrl(),
                getHeadBranch(),
                getTitle()),
            new GitBranchSCMRevision(targetHead, getBaseCommit().getId()),
            new GitBranchSCMRevision(new GitBranchSCMHead(getHeadBranch()), getHeadCommit().getId())
        );
    }

    public enum State {
        open, closed
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getHeadBranch() {
        return headBranch;
    }

    public void setHeadBranch(String headBranch) {
        this.headBranch = headBranch;
    }

    public String getBaseBranch() {
        return baseBranch;
    }

    public void setBaseBranch(String baseBranch) {
        this.baseBranch = baseBranch;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public Boolean getMergeable() {
        return mergeable;
    }

    public void setMergeable(Boolean mergeable) {
        this.mergeable = mergeable;
    }

    public boolean isHasMerged() {
        return hasMerged;
    }

    public void setHasMerged(boolean hasMerged) {
        this.hasMerged = hasMerged;
    }

    public ZonedDateTime getMergedAt() {
        return mergedAt;
    }

    public void setMergedAt(ZonedDateTime mergedAt) {
        this.mergedAt = mergedAt;
    }

    public String getMergedCommitSha() {
        return mergedCommitSha;
    }

    public void setMergedCommitSha(String mergedCommitSha) {
        this.mergedCommitSha = mergedCommitSha;
    }

    public User getMergedBy() {
        return mergedBy;
    }

    public void setMergedBy(User mergedBy) {
        this.mergedBy = mergedBy;
    }

    public Repository getHeadRepo() {
        return headRepo;
    }

    public void setHeadRepo(Repository headRepo) {
        this.headRepo = headRepo;
    }

    public Repository getBaseRepo() {
        return baseRepo;
    }

    public void setBaseRepo(Repository baseRepo) {
        this.baseRepo = baseRepo;
    }

    public Commit getHeadCommit() {
        return headCommit;
    }

    public void setHeadCommit(Commit headCommit) {
        this.headCommit = headCommit;
    }

    public Commit getBaseCommit() {
        return baseCommit;
    }

    public void setBaseCommit(Commit baseCommit) {
        this.baseCommit = baseCommit;
    }

    public boolean isSameOrigin() {
        return sameOrigin;
    }

    public void setSameOrigin(boolean sameOrigin) {
        this.sameOrigin = sameOrigin;
    }
}
