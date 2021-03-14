package io.jenkins.plugins.dagshubbranchsource;

import io.jenkins.plugins.dagshubbranchsource.api.DAGsHubApi;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DAGsHubApiTest {

    @Test
    public void fromRepoUrlTest() throws Exception {
        DAGsHubApi api = DAGsHubApi.create("http://dagshub.com/user/repo");
        assertEquals("http://dagshub.com/api/v1/", api.getApiRootUrl().toString());
        assertEquals("user", api.getOwner());
        assertEquals("repo", api.getRepo());

        api = DAGsHubApi.create("http://dagshub.com/user/repo/");
        assertEquals("http://dagshub.com/api/v1/", api.getApiRootUrl().toString());
        assertEquals("user", api.getOwner());
        assertEquals("repo", api.getRepo());

        api = DAGsHubApi.create("http://dagshub.com/user/repo.git");
        assertEquals("http://dagshub.com/api/v1/", api.getApiRootUrl().toString());
        assertEquals("user", api.getOwner());
        assertEquals("repo", api.getRepo());

        api = DAGsHubApi.create("http://dagshub.com/user/repo.git/");
        assertEquals("http://dagshub.com/api/v1/", api.getApiRootUrl().toString());
        assertEquals("user", api.getOwner());
        assertEquals("repo", api.getRepo());

        api = DAGsHubApi.create("http://dagshub.com/sub/path/user/repo.git/");
        assertEquals("http://dagshub.com/sub/path/api/v1/", api.getApiRootUrl().toString());
        assertEquals("user", api.getOwner());
        assertEquals("repo", api.getRepo());

        api = DAGsHubApi.create("http://different.dagshub.com/sub/path/user/repo.git/");
        assertEquals("http://different.dagshub.com/sub/path/api/v1/", api.getApiRootUrl().toString());
        assertEquals("user", api.getOwner());
        assertEquals("repo", api.getRepo());

        api = DAGsHubApi.create("https://different.dagshub.com/sub/path/user/repo.git/");
        assertEquals("https://different.dagshub.com/sub/path/api/v1/", api.getApiRootUrl().toString());
        assertEquals("user", api.getOwner());
        assertEquals("repo", api.getRepo());

        try {
            DAGsHubApi.create("https://dagshub.com/");
            fail("Should have thrown MalformedURLException");
        } catch (IllegalArgumentException e) {
            // Pass
        }

        try {
            DAGsHubApi.create("https://dagshub.com/user");
            fail("Should have thrown MalformedURLException");
        } catch (IllegalArgumentException e) {
            // Pass
        }

        try {
            DAGsHubApi.create("https://dagshub.com/user/");
            fail("Should have thrown MalformedURLException");
        } catch (IllegalArgumentException e) {
            // Pass
        }
    }
}
