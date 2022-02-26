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

import com.fasterxml.jackson.databind.JsonNode;
import de.griefed.versionchecker.Comparison;
import org.jetbrains.annotations.NotNull;
import de.griefed.versionchecker.VersionChecker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Check a given GitHub repository for updates.<br>
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

    private static final Logger LOG = LogManager.getLogger(GitHubChecker.class);

    private final URL GITHUB_API;
    private final URL GITHUB_API_LATEST;

    private JsonNode repository;
    private JsonNode latest;

    /**
     * Constructs a GitHub checker with the given <code>user/repository</code> combination to allow for version checks as
     * well as version and URL acquisition.
     * @author Griefed
     * @param gitHubUserRepository String. GitHub <code>user/repository</code>-combination. For example <code>Griefed/ServerPackCreator</code>
     * @throws MalformedURLException Thrown if the resulting URL is malformed or otherwise invalid.
     */
    public GitHubChecker(@NotNull String gitHubUserRepository) throws MalformedURLException {
        this.GITHUB_API = new URL("https://api.github.com/repos/" + gitHubUserRepository + "/releases");
        this.GITHUB_API_LATEST = new URL("https://api.github.com/repos/" + gitHubUserRepository + "/releases/latest");
    }

    /**
     * Refresh this GitHub-instance. Refreshes repository information, the latest version, as well as a list of all available
     * versions.
     * @author Griefed
     * @throws IOException Exception thrown if {@link #setRepository()} or {@link #setLatest()} encounter an error.
     * @return This GitHub-instance.
     */
    @Override
    public GitHubChecker refresh() throws IOException {
        setRepository();
        setLatest();
        setAllVersions();

        return this;
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

        LOG.debug("All versions: " + versions);

        // In case the given repository does not have any releases
        if (versions.size() == 0) {
            return null;
        }

        return versions;
    }

    /**
     * Get the latest regular release, or pre-release if <code>checkForPreRelease</code> is <code>true</code>.
     * @author Griefed
     * @param checkForPreRelease Boolean. Whether to include alpha and beta releases for latest release versions.
     * @return String. Returns the latest regular release. If no regular release is available, <code>no_release</code> is returned.
     */
    @Override
    public String latestVersion(boolean checkForPreRelease) {
        if (latest != null) {

            String version = latest.get("tag_name").asText();

            if (checkForPreRelease) {

                String alpha = latestAlpha();
                String beta = latestBeta();

                if (!beta.equals("no_betas") && compareSemantics(version, beta, Comparison.NEW)) {
                    version = beta;
                }

                if (!alpha.equals("no_alphas") && compareSemantics(version, alpha, Comparison.NEW)) {
                    version = alpha;
                }
            }

            LOG.debug("Latest version:" + latest);
            return version;
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
    public String getDownloadUrl(@NotNull String version) {

        if (repository != null) {
            for (JsonNode tag : repository) {
                if (tag.get("tag_name").asText().equals(version)) {
                    return tag.get("html_url").asText();
                }
            }
        }

        return "No URL found.";
    }

    /**
     * Acquire this instances repository information and store it in a {@link JsonNode} for later use.
     * @author Griefed
     * @throws IOException Thrown if the repository can not be reached or any other unexpected error occurs.
     */
    @Override
    protected void setRepository() throws IOException {
        this.repository = getObjectMapper().readTree(getResponse(GITHUB_API));

    }

    /**
     * Acquires the latest version for this instances repository.
     * @author Griefed
     * @throws IOException Thrown if the repository can not be reached or any other unexpected error occurs.
     */
    private void setLatest() throws IOException {
        this.latest = getObjectMapper().readTree(getResponse(GITHUB_API_LATEST));
    }

    /**
     * Get the asset download URLs for a particular tag/version.
     * @author Griefed
     * @param requestedVersion String. The version you want to retrieve the asset download URLs for.
     * @return List String. A list of download URLs for the assets of the given tag/version.
     */
    @Override
    public List<String> getAssetsDownloadUrls(@NotNull String requestedVersion) {

        List<String> assetUrls = new ArrayList<>(20);

        if (repository != null) {
            for (JsonNode version : repository) {

                if (version.get("tag_name").asText().equals(requestedVersion)) {

                    for (JsonNode asset : version.get("assets")) {

                        if (!assetUrls.contains(asset.get("browser_download_url").asText())) {
                            assetUrls.add(asset.get("browser_download_url").asText());
                        }

                    }

                }

            }
        }

        return assetUrls;
    }
}