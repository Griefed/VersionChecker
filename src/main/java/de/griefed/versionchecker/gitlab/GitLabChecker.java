package de.griefed.versionchecker.gitlab;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.griefed.versionchecker.VersionChecker;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Check a given repository, by a given user, for updates.<br>
 * Versions are checked for semantic-release-formatting. Meaning tags must look like the following examples:<br>
 * 2.0.0<br>
 * 2.1.1<br>
 * 3.0.0-alpha.1<br>
 * 3.0.0-alpha.13<br>
 * 1.2.3-beta.1<br>
 * and so on.
 * @author Griefed
 */
public class GitLabChecker extends VersionChecker {

    private final String GITLAB_API;
    private final RestTemplate REST_TEMPLATE = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(5))
            .build();

    /**
     * Constructor for the GitLab checker. Requires the username <code>gitLabBaseUrl</code> from which the repository with
     * the given <code>id</code> will make up the URL called for checks.
     * @author Griefed
     * @param gitLabBaseUrl String. The base URL of the GitLab instance on which you want to check a repository.
     * @param id Int. The id of the project you want to check the releases for.
     */
    public GitLabChecker(String gitLabBaseUrl, int id) {
        this.GITLAB_API = gitLabBaseUrl + "/api/v4/projects/" + id + "/releases";
        setAllVersions();
    }

    /**
     * Gather a list of all available versions for the given repository.
     * @author Griefed
     * @return List String. Returns a list of all available versions. If an error occurred, or no versions are available,
     * <code>null</code> is returned.
     */
    @Override
    public List<String> allVersions() {
        List<String> versions = new ArrayList<>(1000);

        try {
            // Store all available versions in a list
            for (JsonNode version : getObjectMapper().readTree(REST_TEMPLATE.getForEntity(GITLAB_API, String.class).getBody())) {
                if (!versions.contains(version.get("tag_name").asText())) {
                    versions.add(version.get("tag_name").asText());
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            versions = null;
        }

        // In case the given repository does not have any releases
        if (versions == null || versions.size() == 0) {
            return null;
        }

        return versions;
    }

    /**
     * Get the latest regular release.
     * @author Griefed
     * @return String. Returns the latest regular release. If no regular release is available, <code>no_release</code> is returned.
     */
    @Override
    public String latestVersion() {

        for (String version : getAllVersions()) {
            if (!version.contains("alpha") && !version.contains("beta")) {
                return version;
            }
        }

        return "no_release";
    }

    /**
     * Get the URL for the given release version.
     * @author Griefed
     * @param version String. The version for which to get the URL to.
     * @return String. Returns the URL to the given release version.
     */
    @Override
    public String getDownloadUrl(String version) {
        try {
            for (JsonNode tag : getObjectMapper().readTree(REST_TEMPLATE.getForEntity(GITLAB_API, String.class).getBody())) {
                if (tag.get("tag_name").asText().equals(version)) {
                    return tag.get("_links").get("self").asText();
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "No URL found.";
    }

    @Override
    public List<String> getAssetsDownloadUrls(String requestedVersion) {

        List<String> assetUrls = new ArrayList<>(20);

        try {
            for (JsonNode version : getObjectMapper().readTree(REST_TEMPLATE.getForEntity(GITLAB_API, String.class).getBody())) {
                if (version.get("tag_name").asText().equals(requestedVersion)) {
                    //assetUrls.contains(asset.get("url").asText());
                    for (JsonNode asset : version.get("assets").get("links")) {
                        if (!assetUrls.contains(asset.get("url").asText())) {
                            assetUrls.add(asset.get("url").asText());
                        }
                    }
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return assetUrls;
    }
}
