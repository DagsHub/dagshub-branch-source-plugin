package io.jenkins.plugins.dagshubbranchsource;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.TaskListener;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.mixin.ChangeRequestCheckoutStrategy;
import jenkins.scm.api.trait.SCMSourceRequest;

public class DAGsHubSCMSourceRequest extends SCMSourceRequest {

    private final boolean fetchBranches;
    private final boolean fetchTags;
    private final boolean fetchOriginPullRequests;
    private final ChangeRequestCheckoutStrategy originPullStrategy;
    private final boolean fetchForkPullRequests;
    private final ChangeRequestCheckoutStrategy forkPullStrategy;

    protected DAGsHubSCMSourceRequest(
        @NonNull SCMSource source,
        @NonNull DAGsHubSCMSourceContext context,
        TaskListener listener) {
        super(source, context, listener);
        this.fetchBranches = context.isWantBranches();
        this.fetchTags = context.isWantTags();
        this.fetchOriginPullRequests = context.isWantOriginPullRequests();
        this.originPullStrategy = context.getOriginPullStrategy();
        this.fetchForkPullRequests = context.isWantForkPullRequests();
        this.forkPullStrategy = context.getForkPullStrategy();
    }

    public boolean isFetchBranches() {
        return fetchBranches;
    }

    public boolean isFetchTags() {
        return fetchTags;
    }

    public boolean isFetchOriginPullRequests() {
        return fetchOriginPullRequests;
    }

    public boolean isFetchAnyPullRequests() {
        return isFetchOriginPullRequests() || isFetchForkPullRequests();
    }

    public ChangeRequestCheckoutStrategy getOriginPullStrategy() {
        return originPullStrategy;
    }

    public boolean isFetchForkPullRequests() {
        return fetchForkPullRequests;
    }

    public ChangeRequestCheckoutStrategy getForkPullStrategy() {
        return forkPullStrategy;
    }
}
