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
package de.griefed.versionchecker;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Check a given GitLab repository for updates.<br>
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

    private static final Logger LOG = LogManager.getLogger(GitLabChecker.class);

    private final URL GITLAB_API;

    private JsonNode repository;

    /**
     * Constructs a GitLab checker with the given GitLab-URL to allow for version checks as well as version and URL
     * acquisition.
     * @author Griefed
     * @param repositoryUrl String. The full /api/v4-GitLab-repository-URL you want to check. Examples:<br>
     *                   <code>https://gitlab.com/api/v4/projects/32677538/releases</code><br>
     *                   <code>https://git.griefed.de/api/v4/projects/63/releases</code>
     * @throws MalformedURLException Thrown if the resulting URL is malformed or otherwise invalid.
     */
    public GitLabChecker(@NotNull String repositoryUrl) throws MalformedURLException {
        this.GITLAB_API = new URL(repositoryUrl);
    }

    /**
     * Constructs a GitLab checker with the given GitLab-URL to allow for version checks as well as version and URL
     * acquisition.
     * @author Griefed
     * @param repositoryUrl {@link URL}. The full /api/v4-GitLab-repository-URL you want to check. Examples:<br>
     *                   <code>https://gitlab.com/api/v4/projects/32677538/releases</code><br>
     *                   <code>https://git.griefed.de/api/v4/projects/63/releases</code>
     */
    public GitLabChecker(@NotNull URL repositoryUrl) {
        this.GITLAB_API = repositoryUrl;
    }

    /**
     * Refresh this GitLab-instance. Refreshes repository information and the list of all available versions.
     * @author Griefed
     * @throws IOException Exception thrown if {@link #setRepository()} encounters an error.
     * @return This GitLab-instance.
     */
    @Override
    public GitLabChecker refresh() throws IOException  {
        setRepository();
        setAllVersions();

        return this;
    }

    /**
     * Check whether an update/newer version is available for the given version. If you want to check for PreReleases, too,
     * then make sure to pass <code>true</code> for <code>checkForPreReleases</code>.
     * @author Griefed
     * @param currentVersion String. The current version of the app.
     * @param checkForPreReleases Boolean. <code>false</code> if you do not want to check for PreReleases. <code>true</code>
     *                            if you want to check for PreReleases as well.
     * @return {@link Update}-instance, wrapped in an {@link Optional}, contianing information about the available update.
     */
    @Override
    public Optional<Update> check(@NotNull String currentVersion, boolean checkForPreReleases) {
        LOG.debug("Current version: " + currentVersion);

        try {

            String newVersion = isUpdateAvailable(currentVersion, checkForPreReleases);

            if (!newVersion.equals("up_to_date")) {

                String description = "N/A";
                LocalDate releaseDate = null;
                List<ReleaseAsset> assets = new ArrayList<>();
                List<Source> sources = new ArrayList<>();

                for (JsonNode release : repository) {

                    if (release.get("tag_name").asText().equals(newVersion)) {

                        description = release.get("description").asText();

                        releaseDate = LocalDate.parse(release.get("released_at").asText()
                                .substring(0, release.get("released_at").asText().lastIndexOf("T"))
                        );

                        for (JsonNode asset : release.get("assets").get("links")) {

                            assets.add(
                                    new ReleaseAsset(
                                            asset.get("name").asText(),
                                            new URL(asset.get("direct_asset_url").asText())
                                    )
                            );

                        }

                        for (JsonNode source : release.get("assets").get("sources")) {

                            ArchiveType type = null;

                            switch (source.get("format").asText()) {
                                case "zip":
                                    type = ArchiveType.ZIP;
                                    break;
                                case "tar.gz":
                                    type = ArchiveType.TAR_GZ;
                                    break;
                                case "tar.bz2":
                                    type = ArchiveType.TAR_BZ2;
                                    break;
                                case "tar":
                                    type = ArchiveType.TAR;
                                    break;
                            }

                            sources.add(
                                    new Source(
                                            type,
                                            new URL(source.get("url").asText())
                                    )
                            );

                        }

                        break;
                    }
                }

                return Optional.of(
                        new Update(
                                newVersion,
                                description,
                                new URL(getDownloadUrl(newVersion)),
                                releaseDate,
                                assets,
                                sources
                        )
                );

            }

        } catch (NumberFormatException ex) {
            LOG.error("A version could not be parsed into integers.", ex);
        } catch (MalformedURLException ex) {
            LOG.error("URL could not be created.",ex);
        }

        return Optional.empty();
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
     * Get the latest regular release.
     * @author Griefed
     * @param checkForPreRelease Boolean. Whether to include alpha and beta releases for latest release versions.
     * @return String. Returns the latest regular release. If no regular release is available, <code>no_release</code> is returned.
     */
    @Override
    public String latestVersion(boolean checkForPreRelease) {

        if (getAllVersions() != null) {

            String latest = null;

            if (checkForPreRelease) {
                latest = getAllVersions().get(0);
            } else {
                for (String version : getAllVersions()) {
                    if (!version.contains("alpha") && !version.contains("beta")) {
                        latest = version;
                        break;
                    }
                }
            }

            if (latest == null) {
                return "no_release";
            }

            for (String version : getAllVersions()) {

                LOG.debug("version: " + version);

                if (!version.contains("alpha") && !version.contains("beta") && compareSemantics(latest, version, Comparison.NEW)) {

                    latest = version;

                }
            }

            if (checkForPreRelease) {

                String alpha = latestAlpha();
                String beta = latestBeta();

                if (!beta.equals("no_betas") && compareSemantics(latest, beta, Comparison.NEW)) {
                    latest = beta;
                }

                if (!alpha.equals("no_alphas") && compareSemantics(latest, alpha, Comparison.NEW)) {
                    latest = alpha;
                }
            }

            return latest;
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
                    return tag.get("_links").get("self").asText();
                }
            }
        }

        return "No URL found.";
    }

    /**
     * Set the repository JsonNode, for the given <code>GITLAB_API</code>-URL this GitLabChecker-instance was initialized
     * with, so we can retrieve information from it later on.
     * @author Griefed
     * @throws IOException Thrown if the set repository can not be reached or the URL is malformed in any way.
     */
    @Override
    protected void setRepository() throws IOException {
        this.repository = getObjectMapper().readTree(getResponse(GITLAB_API));
    }

    /**
     * Get the asset download URLs for a particular tag/version.
     * @author Griefed
     * @param requestedVersion String. The version you want to retrieve the asset download URLs for.
     * @return List String. A list of download URLs for the assets of the given tag/version.
     * @deprecated The aim of VersionChecker is not to browse a given repository, but to check for availability of updates,
     * and if an update is available, work from there. See {@link #check(String, boolean)} which returns an instance of
     * {@link Update}. This provides everything you need.
     */
    @Deprecated
    @Override
    public List<String> getAssetsDownloadUrls(@NotNull String requestedVersion) {

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