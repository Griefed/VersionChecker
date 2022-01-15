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
package de.griefed.versionchecker.github;

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
public class GitHubChecker extends VersionChecker {

    private final String GITHUB_API;
    private final String GITHUB_API_LATEST;
    private final RestTemplate REST_TEMPLATE = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(5))
            .build();

    /**
     * Constructor for the GitHub checker. Requires the username <code>user</code> for which the given repository
     * <code>repository</code> will make up the URL called for checks.
     * @author Griefed
     * @param user String. The GitHub username who the repository belongs to.
     * @param repository String. The GitHub repository owned by <code>user</code>.
     */
    public GitHubChecker(String user, String repository) {
        this.GITHUB_API = "https://api.github.com/repos/" + user + "/" + repository + "/releases";
        this.GITHUB_API_LATEST = "https://api.github.com/repos/" + user + "/" + repository + "/releases" + "/latest";
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
            for (JsonNode version : getObjectMapper().readTree(REST_TEMPLATE.getForEntity(GITHUB_API, String.class).getBody())) {
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
        //noinspection UnusedAssignment
        String latest = null;
        try {

            latest = getObjectMapper().readTree(REST_TEMPLATE.getForEntity(GITHUB_API_LATEST, String.class).getBody()).get("tag_name").asText();

        } catch (JsonProcessingException e) {

            e.printStackTrace();
            latest = "no_release";

        }

        return latest;
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
            for (JsonNode tag : getObjectMapper().readTree(REST_TEMPLATE.getForEntity(GITHUB_API, String.class).getBody())) {
                if (tag.get("tag_name").asText().equals(version)) {
                    return tag.get("html_url").asText();
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "No URL found.";
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

        try {
            for (JsonNode version : getObjectMapper().readTree(REST_TEMPLATE.getForEntity(GITHUB_API, String.class).getBody())) {
                if (version.get("tag_name").asText().equals(requestedVersion)) {
                    for (JsonNode asset : version.get("assets")) {
                        if (!assetUrls.contains(asset.get("browser_download_url").asText())) {
                            assetUrls.add(asset.get("browser_download_url").asText());
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
