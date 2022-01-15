/*
 * MIT License
 *
 * Copyright (c) 2022 Griefed
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.griefed.versionchecker.gitlab;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.griefed.versionchecker.VersionChecker;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.HttpClientErrorException;
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

    private JsonNode repository;

    /**
     * Constructor for the GitLab checker. Requires the username <code>gitLabBaseUrl</code> from which the repository with
     * the given <code>id</code> will make up the URL called for checks.
     * @author Griefed
     * @param gitLabBaseUrl String. The base URL of the GitLab instance on which you want to check a repository.
     * @param id Int. The id of the project you want to check the releases for.
     */
    public GitLabChecker(String gitLabBaseUrl, int id) {
        this.GITLAB_API = gitLabBaseUrl + "/api/v4/projects/" + id + "/releases";
        try {
            setRepository();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            this.repository = null;
        }
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

        if (repository != null) {
            // Store all available versions in a list
            for (JsonNode version : repository) {
                if (!versions.contains(version.get("tag_name").asText())) {
                    versions.add(version.get("tag_name").asText());
                }
            }
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
        if (repository != null) {
            for (JsonNode tag : repository) {
                if (tag.get("tag_name").asText().equals(version)) {
                    return tag.get("_links").get("self").asText();
                }
            }
        }

        return "No URL found.";
    }

    @Override
    protected void setRepository() throws JsonProcessingException, HttpClientErrorException {
        this.repository = getObjectMapper().readTree(REST_TEMPLATE.getForEntity(GITLAB_API, String.class).getBody());
    }

    /**
     * Get the asset download URLs for a particular tag/version.
     * @author Griefed
     * @param requestedVersion String. The version you want to retrieve the asset download URLs for.
     * @return List String. A list of download URLs for the assets of the given tag/version.
     */
    @Override
    public List<String> getAssetsDownloadUrls(String requestedVersion) {

        List<String> assetUrls = new ArrayList<>(20);

        if (repository != null) {
            for (JsonNode version : repository) {

                if (version.get("tag_name").asText().equals(requestedVersion)) {

                    for (JsonNode asset : version.get("assets").get("links")) {

                        if (!assetUrls.contains(asset.get("url").asText())) {
                            assetUrls.add(asset.get("url").asText());
                        }

                    }

                }

            }
        }

        return assetUrls;
    }
}
