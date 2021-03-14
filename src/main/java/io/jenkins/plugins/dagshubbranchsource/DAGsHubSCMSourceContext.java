package io.jenkins.plugins.dagshubbranchsource;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.TaskListener;
import jenkins.scm.api.SCMHeadObserver;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceCriteria;
import jenkins.scm.api.mixin.ChangeRequestCheckoutStrategy;
import jenkins.scm.api.trait.SCMSourceContext;

public class DAGsHubSCMSourceContext extends SCMSourceContext<DAGsHubSCMSourceContext, DAGsHubSCMSourceRequest> {

    private boolean wantBranches;
    private boolean wantTags;
    private boolean wantOriginPullRequests;
    private ChangeRequestCheckoutStrategy originPullStrategy;
    private boolean wantForkPullRequests;
    private ChangeRequestCheckoutStrategy forkPullStrategy;

    public DAGsHubSCMSourceContext(SCMSourceCriteria criteria, @NonNull SCMHeadObserver observer) {
        super(criteria, observer);
    }

    @NonNull
    @Override
    public DAGsHubSCMSourceRequest newRequest(
        @NonNull SCMSource source,
        TaskListener listener) {
        return new DAGsHubSCMSourceRequest(source, this, listener);
    }

    public boolean isWantBranches() {
        return wantBranches;
    }

    public void setWantBranches(boolean wantBranches) {
        this.wantBranches = wantBranches;
    }

    public boolean isWantTags() {
        return wantTags;
    }

    public void setWantTags(boolean wantTags) {
        this.wantTags = wantTags;
    }

    public boolean isWantOriginPullRequests() {
        return wantOriginPullRequests;
    }

    public void setWantOriginPullRequests(boolean wantOriginPullRequests) {
        this.wantOriginPullRequests = wantOriginPullRequests;
    }

    public boolean isWantForkPullRequests() {
        return wantForkPullRequests;
    }

    public void setWantForkPullRequests(boolean wantForkPullRequests) {
        this.wantForkPullRequests = wantForkPullRequests;
    }

    public ChangeRequestCheckoutStrategy getOriginPullStrategy() {
        return originPullStrategy;
    }

    public void setOriginPullStrategy(ChangeRequestCheckoutStrategy originPullStrategy) {
        this.originPullStrategy = originPullStrategy;
    }

    public ChangeRequestCheckoutStrategy getForkPullStrategy() {
        return forkPullStrategy;
    }

    public void setForkPullStrategy(ChangeRequestCheckoutStrategy forkPullStrategy) {
        this.forkPullStrategy = forkPullStrategy;
    }
}
