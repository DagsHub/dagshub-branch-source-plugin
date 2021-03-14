package io.jenkins.plugins.dagshubbranchsource.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jenkins.plugins.git.GitBranchSCMHead;
import jenkins.plugins.git.GitBranchSCMRevision;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Branch {
    private String name;
    private Commit commit;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public GitBranchSCMRevision toRev() {
        return new GitBranchSCMRevision(
                new GitBranchSCMHead(getName()),
                getCommit().getId());
    }
}
