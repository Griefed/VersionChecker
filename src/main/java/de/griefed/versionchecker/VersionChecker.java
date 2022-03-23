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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Baseclass from wich GitHub and GitLab checks extend. This class mainly provides the logic for comparing versions against
 * each other to find out which is newer. Extend from this class if you want to implement your own checkers, for platforms
 * like Gitea or anything else.
 * @author Griefed
 */
public abstract class VersionChecker {

    private static final Logger LOG = LogManager.getLogger(VersionChecker.class);

    private List<String> allVersions;

    /**
     * Check whether an update/newer version is available for the given version. If you want to check for PreReleases, too,
     * then make sure to pass <code>true</code> for <code>checkForPreReleases</code>.
     * @author Griefed
     * @param currentVersion String. The current version of the app.
     * @param checkForPreReleases Boolean. <code>false</code> if you do not want to check for PreReleases. <code>true</code>
     *                            if you want to check for PreReleases as well.
     * @return String. Returns a concatenated String whether an update is available or not. Examples:<br>
     * No update available: <code>No updates available.</code><br>
     * New release available: <code>Current version: 2.0.0. A new release is available: 2.1.1. Download available at: https://github.com/Griefed/ServerPackCreator/releases/tag/2.1.1</code>
     * New prerelease available: <code>Current version: 2.0.0. A new PreRelease is available: 3.0.0-alpha.14. Download available at: https://github.com/Griefed/ServerPackCreator/releases/tag/3.0.0-alpha.14</code>
     */
    public String checkForUpdate(@NotNull String currentVersion, boolean checkForPreReleases) {

        LOG.debug("Current version: " + currentVersion);

        try {

            String newVersion = isUpdateAvailable(currentVersion, checkForPreReleases);

            if (newVersion.equals("up_to_date")) {
                return "No updates available.";
            } else {
                return newVersion + ";" + getDownloadUrl(newVersion);
            }

        } catch (NumberFormatException ex) {
            LOG.error("A version could not be parsed into integers.", ex);
            return "No updates available.";
        }
    }

    /**
     * Check for new versions in beta, alpha and regular release channels. If <code>checkForPreRelease</code> is false,
     * only regular releases are checked.
     * @author Griefed
     * @param currentVersion String. The current version of the app.
     * @param checkForPreReleases Boolean. <code>false</code> if you do not want to check for PreReleases. <code>true</code>
     *                            if you want to check for PreReleases as well.
     * @return String. Returns the available update version. If no update is available, then <code>up_to_date</code> is returned.
     */
    private String isUpdateAvailable(@NotNull String currentVersion, boolean checkForPreReleases) {

        if (checkForPreReleases) {
            if (isNewBetaAvailable(currentVersion)) {
                return latestBeta();
            }

            if (isNewAlphaAvailable(currentVersion)) {
                return latestAlpha();
            }
        }

        if (compareSemantics(currentVersion, latestVersion(checkForPreReleases), Comparison.NEW)) {

            return latestVersion(checkForPreReleases);

        } else if (currentVersion.matches("\\d+\\.\\d+\\.\\d+-(alpha|beta)\\.\\d+") &&
                compareSemantics(currentVersion, latestVersion(false), Comparison.EQUAL)) {

            return latestVersion(false);

        }

        return "up_to_date";
    }

    /**
     * Compare the given new version against the given current version, depending on comparison type <code>EQUAL</code>,
     * <code>NEW</code>, or <code>EQUAL_OR_NEW</code>.
     * Checks are performed with the semantic release-formatting, e.g. 1.2.3, 2.3.4, 6.6.6
     * @author Griefed
     * @param currentVersion String. Current version to check against <code>newVersion</code>.
     * @param newVersion String. New version to check against <code>currentVersion</code>.
     * @param comparison {@link Comparison} Comparison level. Either <code>EQUAL</code>, <code>NEW</code>, or <code>EQUAL_OR_NEW</code>.
     * @return Boolean. Returns <code>true</code> if the new version is indeed newer than the current version. Otherwise
     * <code>false</code>.
     * @throws NumberFormatException Thrown if the passed <code>currentVersion</code> or <code>newVersion</code> can not be
     * parsed into integers.
     */
    protected boolean compareSemantics(@NotNull String currentVersion, @NotNull String newVersion, @NotNull Comparison comparison) {

        LOG.debug("Current version: " + currentVersion);
        LOG.debug("New version:     " + newVersion);
        LOG.debug("Comparison:      " + comparison);

        if (newVersion.equals("no_release")) {
            return false;
        }

        int newMajor,newMinor,newPatch,currentMajor,currentMinor,currentPatch;

        List<Integer> currentSemantics = getSemantics(currentVersion);
        List<Integer> newSemantics = getSemantics(newVersion);

        currentMajor = currentSemantics.get(0);
        currentMinor = currentSemantics.get(1);
        currentPatch = currentSemantics.get(2);

        newMajor = newSemantics.get(0);
        newMinor = newSemantics.get(1);
        newPatch = newSemantics.get(2);

        switch (comparison) {

            case EQUAL:
                return checkEqual(newMajor,newMinor,newPatch,currentMajor,currentMinor,currentPatch);

            case NEW:
                return checkNew(newMajor,newMinor,newPatch,currentMajor,currentMinor,currentPatch);

            case EQUAL_OR_NEW:
                return checkNewOrEqual(newMajor,newMinor,newPatch,currentMajor,currentMinor,currentPatch);

            default:
                LOG.error("Incorrect comparison type selected: " + comparison);
                return false;

        }
    }

    /**
     * Acquire the version numbers of a semantic-formatted version.
     * @author Griefed
     * @param version String. The version from which to acquire the version numbers.
     * @return Integer List. List of version numbers. Major index 0, minor index 1, patch index 2.
     */
    private List<Integer> getSemantics(String version) {
        List<Integer> semantics = new ArrayList<>(3);

        String[] versionNumbers = version.split("\\.");

        semantics.add(0,Integer.parseInt(versionNumbers[0]));
        semantics.add(1,Integer.parseInt(versionNumbers[1]));
        if (versionNumbers[2].contains("-")) {

            semantics.add(2,Integer.parseInt(versionNumbers[2].split("-")[0]));

        } else {

            semantics.add(2,Integer.parseInt(versionNumbers[2]));

        }

        return semantics;
    }

    /**
     * Compare two versions against each other and determine whether the new version is a newer semantic release version.
     * @author Griefed
     * @param newMajor Integer. The new versions major number.
     * @param newMinor Integer. The new versions minor number.
     * @param newPatch Integer. The new versions patch number.
     * @param currentMajor Integer. The old versions major number.
     * @param currentMinor Integer. The old versions minor number.
     * @param currentPatch Integer. The old versions patch number.
     * @return Boolean. True if a new version was determined.
     */
    private boolean checkNew(int newMajor, int newMinor, int newPatch, int currentMajor, int currentMinor, int currentPatch) {
        if (newMajor > currentMajor) {
            // new major update
            return true;

        } else if (newMajor == currentMajor && newMinor > currentMinor) {
            // new minor update
            return true;

            // new patch update if true
        } else
            return newMajor == currentMajor && newMinor == currentMinor && newPatch > currentPatch;
    }

    /**
     * Compare two versions against each other and determine whether the new version is a newer or the same semantic
     * release version.
     * @author Griefed
     * @param newMajor Integer. The new versions major number.
     * @param newMinor Integer. The new versions minor number.
     * @param newPatch Integer. The new versions patch number.
     * @param currentMajor Integer. The old versions major number.
     * @param currentMinor Integer. The old versions minor number.
     * @param currentPatch Integer. The old versions patch number.
     * @return Boolean. True if a new or equal version was determined.
     */
    private boolean checkNewOrEqual(int newMajor, int newMinor, int newPatch, int currentMajor, int currentMinor, int currentPatch) {
        return newMajor >= currentMajor && newMinor >= currentMinor && newPatch >= currentPatch;
    }

    /**
     * Compare two versions against each other and determine whether they are the same semantic versions.
     * @author Griefed
     * @param newMajor Integer. The new versions major number.
     * @param newMinor Integer. The new versions minor number.
     * @param newPatch Integer. The new versions patch number.
     * @param currentMajor Integer. The old versions major number.
     * @param currentMinor Integer. The old versions minor number.
     * @param currentPatch Integer. The old versions patch number.
     * @return Boolean. True if the versions are the same.
     */
    private boolean checkEqual(int newMajor, int newMinor, int newPatch, int currentMajor, int currentMinor, int currentPatch) {
        return newMajor == currentMajor && newMinor == currentMinor && newPatch == currentPatch;
    }

    /**
     * Check for a new alpha version.
     * @author Griefed
     * @param currentVersion String. The current version to check against available alpha versions.
     * @return Boolean. Returns true if a new alpha release is found.
     * @throws NumberFormatException Thrown if the passed <code>currentVersion</code> can not be
     * parsed into integers.
     */
    private boolean isNewAlphaAvailable(@NotNull String currentVersion) throws NumberFormatException {

        // If no alpha releases are available, do not check for new alpha release.
        if (latestAlpha().equals("no_alphas")) {
            return false;
        }

        String latestAlpha = latestAlpha();

        if (compareSemantics(currentVersion, latestAlpha, Comparison.EQUAL) && currentVersion.contains("beta")) {
            return false;
        }

        // Check if the given version is older than the latest alpha version by checking semantically. (1.2.3, 2.3.4, 6.6.6)
        if (compareSemantics(currentVersion, latestAlpha, Comparison.NEW)) {

            return true;

        } else if (compareSemantics(currentVersion, latestAlpha, Comparison.EQUAL_OR_NEW) && currentVersion.contains("-")) {
            // If a new alpha, say alpha.5 for the given, say alpha.1, is available, return true.
            return isPreReleaseNewer(currentVersion, latestAlpha);

        } else {

            return false;

        }
    }

    /**
     * Check for a new beta version.
     * @author Griefed
     * @param currentVersion String. The current version to check against available beta versions.
     * @return Boolean. Returns true if a new beta release is found.
     * @throws NumberFormatException Thrown if the passed <code>currentVersion</code> can not be
     * parsed into integers.
     */
    private boolean isNewBetaAvailable(@NotNull String currentVersion) throws NumberFormatException {

        // If no beta releases are available, do not check for new beta release.
        if (latestBeta().equals("no_betas")) {
            return false;
        }

        String latestBeta = latestBeta();

        // Check if the given version is older than the latest beta version by checking semantically. (1.2.3, 2.3.4, 6.6.6)
        if (compareSemantics(currentVersion, latestBeta, Comparison.NEW)) {

            return true;

        } else if (compareSemantics(currentVersion, latestBeta, Comparison.EQUAL_OR_NEW) && currentVersion.contains("-")) {

            // If a new beta, say beta.5 for the given, say beta.1, is available, return true.
            return isPreReleaseNewer(currentVersion, latestBeta);

        } else {

            return false;
        }
    }

    /**
     * Check whether the release number for the new version is bigger than the one of the current version, indicating a
     * newer pre-release is available.
     * @author Griefed
     * @param currentVersion String. The current version for which we want to check for newer versions availability.
     * @param newVersion String. The new version with which we want to check if it is indeed newer than the current version.
     * @return Boolean. True if the new version is a newer pre-release.
     */
    private boolean isPreReleaseNewer(@NotNull String currentVersion, @NotNull String newVersion) {

        int currentVersionReleaseNumber = Integer.parseInt(
                currentVersion.split("-")[1].split("\\.")[1]
        );

        int newVersionReleaseNumber = Integer.parseInt(
                newVersion.split("-")[1].split("\\.")[1]
        );

        return newVersionReleaseNumber > currentVersionReleaseNumber;
    }

    /**
     * Gather all beta versions in a list.
     * @author Griefed
     * @return List String. Returns a list of all available beta versions. If no beta releases are available for the given
     * repository, <code>null</code> is returned.
     */
    private List<String> allBetaVersions() {

        List<String> betaVersions = new ArrayList<>(1000);

        // Check all available versions whether they contain beta and add them to a list of beta versions.
        if (allVersions != null) {
            for (String version : allVersions) {
                if (version.contains("beta") && !betaVersions.contains(version)) {
                    betaVersions.add(version);
                }
            }
        }

        if (betaVersions.size() == 0) {
            return null;
        } else {
            return betaVersions;
        }
    }

    /**
     * Gather all alpha versions in a list.
     * @author Griefed
     * @return List String. Returns a list of all available alpha versions. If no alpha releases are available for the given
     * repository, <code>null</code> is returned.
     */
    private List<String> allAlphaVersions() {

        List<String> alphaVersions = new ArrayList<>(1000);

        // Check all available versions whether they contain alpha and add them to a list of alpha versions.
        if (allVersions != null) {
            for (String version : allVersions) {
                if (version.contains("alpha") && !alphaVersions.contains(version)) {
                    alphaVersions.add(version);
                }
            }
        }

        if (alphaVersions.size() == 0) {
            return null;
        } else {
            return alphaVersions;
        }
    }

    /**
     * Get the latest beta release.
     * @author Griefed
     * @return String. Returns the latest beta release. If no beta release is available, <code>no_betas</code> is returned.
     * @throws NumberFormatException Thrown if a version can not be parsed into integers.
     */
    protected String latestBeta() throws NumberFormatException {

        List<String> betaVersions = allBetaVersions();
        String beta = "no_betas";

        if (betaVersions != null) {

            beta = betaVersions.get(0);

            for (String betaVersion : betaVersions) {

                if (compareSemantics(beta, betaVersion, Comparison.EQUAL_OR_NEW) && isPreReleaseNewer(beta, betaVersion)) {
                    beta = betaVersion;
                }
            }

        }

        LOG.debug("Latest beta: " + beta);

        return beta;
    }

    /**
     * Get the latest alpha release.
     * @author Griefed
     * @return String. Returns the latest alpha release. If no alpha release is available, <code>no_alphas</code> is returned.
     * @throws NumberFormatException Thrown if a versions can not be parsed into integers.
     */
    protected String latestAlpha() throws NumberFormatException {

        List<String> alphaVersions = allAlphaVersions();
        String alpha = "no_alphas";

        if (alphaVersions != null) {

            alpha = alphaVersions.get(0);

            for (String alphaVersion : alphaVersions) {

                if (compareSemantics(alpha, alphaVersion, Comparison.EQUAL_OR_NEW) && isPreReleaseNewer(alpha, alphaVersion)) {
                    alpha = alphaVersion;
                }
            }

        }

        LOG.debug("Latest alpha: " + alpha);

        return alpha;
    }

    /**
     * Acquire the response from a given URL.
     * @author Griefed
     * @param requestUrl String. The URL to get the response from.
     * @return String. The response from the given URL.
     * @throws IOException Thrown if the requested URL can not be reached or if any other error occurs during the request.
     */
    protected String getResponse(@NotNull URL requestUrl) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) requestUrl.openConnection();
        httpURLConnection.setRequestMethod("GET");

        if (httpURLConnection.getResponseCode() != 200)
            throw new IOException("Request for " + requestUrl + " responded with " + httpURLConnection.getResponseCode());

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(httpURLConnection.getInputStream())
        );

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }

        bufferedReader.close();

        return response.toString();
    }

    protected ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        return objectMapper;
    }

    protected abstract List<String> allVersions();

    public abstract VersionChecker refresh() throws IOException;

    protected void setAllVersions() {
        this.allVersions = allVersions();
    }

    protected List<String> getAllVersions() {
        return allVersions;
    }

    protected abstract String latestVersion(boolean checkForPreRelease);

    protected abstract String getDownloadUrl(@NotNull String version);

    protected abstract void setRepository() throws IOException;

    public abstract List<String> getAssetsDownloadUrls(@NotNull String version);

}