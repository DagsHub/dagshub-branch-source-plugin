package io.jenkins.plugins.dagshubbranchsource.api;

import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

public class DAGsHubApi implements Closeable {

    private final String owner;
    private final String repo;
    private final URI apiRootUrl;
    private final Client client;

    public static DAGsHubApi create(String repoUrl) throws URISyntaxException {
        return create(repoUrl, null, null);
    }

    public static DAGsHubApi create(String repoUrl, String user, String password) throws URISyntaxException {
        // TODO: Support SSH
        if (repoUrl.endsWith("/")) {
            repoUrl = repoUrl.substring(0, repoUrl.length() - 1);
        }
        if (repoUrl.endsWith(".git")) {
            repoUrl = repoUrl.substring(0, repoUrl.length() - 4);
        }
        final URI url = new URI(repoUrl);
        final String urlPath = url.getPath();

        // Make sure the path ends with /user/repo
        // TODO: Better regex, match only allowed chars
        if (!urlPath.matches(".*/[^/]+/[^/]+$")) {
            throw new IllegalArgumentException("Not a valid DAGsHub repo URL: " + urlPath);
        }

        final URI apiUrl = url.resolve("../api/v1/");

        int lastSlashIndex = urlPath.lastIndexOf('/');
        final String repo = urlPath.substring(lastSlashIndex + 1);

        final String pathWithoutRepo = urlPath.substring(0, lastSlashIndex);
        lastSlashIndex = pathWithoutRepo.lastIndexOf('/');
        final String owner = pathWithoutRepo.substring(lastSlashIndex + 1);

        return new DAGsHubApi(owner, repo, apiUrl, user, password);
    }

    public static DAGsHubApi create(String repositoryUrl, StandardUsernameCredentials credentials)
        throws URISyntaxException {
        String user = null, password = null;
        if (credentials != null) {
            user = credentials.getUsername();
            if (credentials instanceof StandardUsernamePasswordCredentials) {
                password = ((StandardUsernamePasswordCredentials) credentials).getPassword().getPlainText();
            } else {
                password = "";
            }
        }
        return create(repositoryUrl, user, password);
    }

    protected DAGsHubApi(String owner, String repo, URI apiRootUrl, String user, String password) {
        this.owner = owner;
        this.repo = repo;
        this.apiRootUrl = apiRootUrl;
        final ClientBuilder clientBuilder = ClientBuilder.newBuilder()
            .register(new JacksonJaxbJsonProvider(
                new ObjectMapper().registerModule(new JavaTimeModule()),
                JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS));

        if (user != null) {
            clientBuilder.register(HttpAuthenticationFeature.basic(user, password));
        }
        this.client = clientBuilder.build();
    }

    public String getOwner() {
        return owner;
    }

    public String getRepo() {
        return repo;
    }

    public URI getApiRootUrl() {
        return apiRootUrl;
    }

    public List<Branch> getBranches() throws IOException {
        final WebTarget webTarget = client.target(apiRootUrl)
            .path("/repos/" + owner + "/" + repo + "/branches");
        // TODO: Paging could be required
        final GenericType<List<Branch>> entity = new GenericType<List<Branch>>() {};
        return webTarget.request(MediaType.APPLICATION_JSON).get(entity);
    }

    public Branch getBranch(String name) throws IOException {
        final WebTarget webTarget = client.target(apiRootUrl)
            .path("/repos/" + owner + "/" + repo + "/branches/" + name);
        return webTarget.request(MediaType.APPLICATION_JSON).get(Branch.class);
    }

    public List<Tag> getTags() throws IOException {
        final WebTarget webTarget = client.target(apiRootUrl)
            .path("/repos/" + owner + "/" + repo + "/tags");
        // TODO: Paging could be required
        final GenericType<List<Tag>> entity = new GenericType<List<Tag>>() {};
        return webTarget.request(MediaType.APPLICATION_JSON).get(entity);
    }

    public Tag getTag(String name) throws IOException {
        final WebTarget webTarget = client.target(apiRootUrl)
            .path("/repos/" + owner + "/" + repo + "/tags/" + name);
        return webTarget.request(MediaType.APPLICATION_JSON).get(Tag.class);
    }

    public PullRequest getPull(long index) throws IOException {
        final WebTarget webTarget = client.target(apiRootUrl)
            .path("/repos/" + owner + "/" + repo + "/pulls/" + index);

        return webTarget.request(MediaType.APPLICATION_JSON).get(PullRequest.class);
    }

    public List<PullRequest> getPulls() {
        final WebTarget webTarget = client.target(apiRootUrl)
            .path("/repos/" + owner + "/" + repo + "/pulls");
        // TODO: Paging could be required
        final GenericType<List<PullRequest>> entity = new GenericType<List<PullRequest>>() {};
        return webTarget.request(MediaType.APPLICATION_JSON).get(entity);
    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}
