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

import java.util.ArrayList;
import java.util.List;

public abstract class VersionChecker {

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
    public String checkForUpdate(String currentVersion, boolean checkForPreReleases) {
        String check = isUpdateAvailable(currentVersion, checkForPreReleases);
        String noUpdateMessage = "No updates available.";
        String updateMessage = "A new release is available:";

        if (checkForPreReleases) {
            noUpdateMessage = noUpdateMessage + " No PreReleases available.";
            updateMessage = "A new PreRelease is available: ";
        }

        if (check.equals("up_to_date")) {
            return noUpdateMessage;
        } else {
            return "Current version: " + currentVersion + ". " + updateMessage + check + ". Download available at: " + getDownloadUrl(check);
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
    private String isUpdateAvailable(String currentVersion, boolean checkForPreReleases) {

        if (isNewBetaAvailable(currentVersion, checkForPreReleases) && checkForPreReleases) return latestBeta();

        if (isNewAlphaAvailable(currentVersion, checkForPreReleases) && checkForPreReleases) return latestAlpha();

        if (isNewSemanticVersion(currentVersion, latestVersion())) return latestVersion();

        return "up_to_date";
    }

    /**
     * Check the given current version against the given new version whether the new version is actually newer.
     * Checks are performed with the semantic release-formatting, e.g. 1.2.3, 2.3.4, 6.6.6
     * @author Griefed
     * @param currentVersion String. Current version to check against <code>newVersion</code>.
     * @param newVersion String. New version to check against <code>currentVersion</code>.
     * @return Boolean. Returns <code>true</code> if the new version is indeed newer than the current version. Otherwise
     * <code>false</code>.
     */
    private boolean isNewSemanticVersion(String currentVersion, String newVersion) {
        if (Integer.parseInt(newVersion.substring(0,1)) > Integer.parseInt(currentVersion.substring(0,1))) {
            return true;
        } else if (Integer.parseInt(newVersion.substring(2,3)) > Integer.parseInt(currentVersion.substring(2,3))) {
            return true;
        } else return Integer.parseInt(newVersion.substring(4,5)) > Integer.parseInt(currentVersion.substring(4,5));
    }

    /**
     * Check the given current version against the given new version whether the new version is actually newer or the same.
     * Checks are performed with the semantic release-formatting, e.g. 1.2.3, 2.3.4, 6.6.6
     * @author Griefed
     * @param currentVersion String. Current version to check against <code>newVersion</code>.
     * @param newVersion String. New version to check against <code>currentVersion</code>.
     * @return Boolean. Returns <code>true</code> if the new version is newer than or equal to the current version. Otherwise
     * <code>false</code>.
     */
    private boolean isNewOrSameSemanticVersion(String currentVersion, String newVersion) {
        if (Integer.parseInt(newVersion.substring(0,1)) >= Integer.parseInt(currentVersion.substring(0,1))) {
            return true;
        } else if (Integer.parseInt(newVersion.substring(2,3)) >= Integer.parseInt(currentVersion.substring(2,3))) {
            return true;
        } else return Integer.parseInt(newVersion.substring(4,5)) >= Integer.parseInt(currentVersion.substring(4,5));
    }

    /**
     * Check for a new alpha version.
     * @author Griefed
     * @param currentVersion String. The current version to check against available alpha versions.
     * @param checkForPreRelease Boolean. Whether to check for PreReleases.
     * @return Boolean. Returns true if a new alpha release is found.
     */
    private boolean isNewAlphaAvailable(String currentVersion, boolean checkForPreRelease) {

        /*
         * If the current version does not contain alpha and checkForPreRelease is false, do not check for a new alpha
         * release.
         */
        if (!currentVersion.contains("alpha") && !checkForPreRelease) return false;

        // If no alpha releases are available, do not check for new alpha release.
        if (latestAlpha().equals("no_alphas")) return false;

        String latestAlpha = latestAlpha();

        // Check if the given version is older than the latest alpha version by checking semantically. (1.2.3, 2.3.4, 6.6.6)
        if (isNewSemanticVersion(currentVersion, latestAlpha)) return true;

        // If a new alpha, say alpha.5 for the given, say alpha.1, is available, return true.
        return Integer.parseInt(latestAlpha.substring(12)) > Integer.parseInt(currentVersion.substring(12));

    }

    /**
     * Check for a new beta version.
     * @author Griefed
     * @param currentVersion String. The current version to check against available beta versions.
     * @param checkForPreRelease Boolean. Whether to check for PreReleases.
     * @return Boolean. Returns true if a new beta release is found.
     */
    private boolean isNewBetaAvailable(String currentVersion, boolean checkForPreRelease) {

        /*
         * If the current version does not contain beta and checkForPreRelease is false, do not check for a new beta
         * release.
         */
        if (!currentVersion.contains("beta") && !checkForPreRelease) return false;

        // If no beta releases are available, do not check for new beta release.
        if (latestBeta().equals("no_betas")) return false;

        String latestBeta = latestBeta();

        // Check if the given version is older than the latest beta version by checking semantically. (1.2.3, 2.3.4, 6.6.6)
        if (isNewSemanticVersion(currentVersion, latestBeta)) return true;

        // If a new beta, say beta.5 for the given, say beta.1, is available, return true.
        return Integer.parseInt(latestBeta.substring(11)) > Integer.parseInt(currentVersion.substring(11));

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

    protected abstract List<String> allVersions();

    /**
     * Get the latest beta release.
     * @author Griefed
     * @return String. Returns the latest beta release. If no beta release is available, <code>no_betas</code> is returned.
     */
    private String latestBeta() {

        List<String> betaVersions = allBetaVersions();
        String beta = "no_betas";

        if (betaVersions != null) {

            beta = betaVersions.get(0);

            for (String betaVersion : betaVersions) {

                if (isNewOrSameSemanticVersion(beta, betaVersion) && Integer.parseInt(betaVersion.substring(11)) > Integer.parseInt(beta.substring(11))) {
                    beta = betaVersion;
                }
            }

        }

        return beta;
    }

    /**
     * Get the latest alpha release.
     * @author Griefed
     * @return String. Returns the latest alpha release. If no alpha release is available, <code>no_alphas</code> is returned.
     */
    private String latestAlpha() {

        List<String> alphaVersions = allAlphaVersions();
        String alpha = "no_alphas";

        if (alphaVersions != null) {

            alpha = alphaVersions.get(0);

            for (String alphaVersion : alphaVersions) {

                if (isNewOrSameSemanticVersion(alpha, alphaVersion) && Integer.parseInt(alphaVersion.substring(12)) > Integer.parseInt(alpha.substring(12))) {
                    alpha = alphaVersion;
                }
            }

        }

        return alpha;
    }

    protected void setAllVersions() {
        this.allVersions = allVersions();
    }

    protected List<String> getAllVersions() {
        return allVersions;
    }

    protected abstract String latestVersion();

    protected abstract String getDownloadUrl(String version);

    public abstract List<String> getAssetsDownloadUrls(String version);

    protected ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        return objectMapper;
    }

}
