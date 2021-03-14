package io.jenkins.plugins.dagshubbranchsource.api;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Repository {

    private long id;
    private User owner;
    private String name;
    private String fullName;
    private String description;
    private @JsonAlias("private") boolean isPrivate;
    private boolean fork;
    private Repository parent;
    private boolean empty;
    private boolean mirror;
    private long size;
    private String htmlUrl;
    private String sshUrl;
    private String cloneUrl;
    private String website;
    private int starsCount;
    private int forksCount;
    private int watchersCount;
    private int openIssuesCount;
    private String defaultBranch;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    /*
	Permissions   *Permission `json:"permissions,omitempty"`
     */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    public Repository getParent() {
        return parent;
    }

    public void setParent(Repository parent) {
        this.parent = parent;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public boolean isMirror() {
        return mirror;
    }

    public void setMirror(boolean mirror) {
        this.mirror = mirror;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getSshUrl() {
        return sshUrl;
    }

    public void setSshUrl(String sshUrl) {
        this.sshUrl = sshUrl;
    }

    public String getCloneUrl() {
        return cloneUrl;
    }

    public void setCloneUrl(String cloneUrl) {
        this.cloneUrl = cloneUrl;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getStarsCount() {
        return starsCount;
    }

    public void setStarsCount(int starsCount) {
        this.starsCount = starsCount;
    }

    public int getForksCount() {
        return forksCount;
    }

    public void setForksCount(int forksCount) {
        this.forksCount = forksCount;
    }

    public int getWatchersCount() {
        return watchersCount;
    }

    public void setWatchersCount(int watchersCount) {
        this.watchersCount = watchersCount;
    }

    public int getOpenIssuesCount() {
        return openIssuesCount;
    }

    public void setOpenIssuesCount(int openIssuesCount) {
        this.openIssuesCount = openIssuesCount;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
