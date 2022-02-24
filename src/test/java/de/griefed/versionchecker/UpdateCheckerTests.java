package de.griefed.versionchecker;

import de.griefed.versionchecker.github.GitHubChecker;
import de.griefed.versionchecker.gitlab.GitLabChecker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class UpdateCheckerTests {

    private static final Logger LOG = LogManager.getLogger(UpdateCheckerTests.class);

    private GitHubChecker GITHUB;
    private GitLabChecker GITGRIEFED;
    private GitLabChecker GITLAB;

    public UpdateCheckerTests() throws IOException {

        this.GITHUB = new GitHubChecker("Griefed/ServerPackCreator");
        this.GITLAB = new GitLabChecker("https://gitlab.com/api/v4/projects/32677538/releases");
        this.GITGRIEFED = new GitLabChecker("https://git.griefed.de/api/v4/projects/63/releases");
    }

    @Test
    void checkForUpdates() {
        try {
            Assertions.assertNotNull(this.GITHUB.refresh());
        } catch (Exception ex) {
            LOG.error("Error refreshing GitHub.", ex);
            this.GITHUB = null;
        }
        try {
            Assertions.assertNotNull(this.GITLAB.refresh());
        } catch (Exception ex) {
            LOG.error("Error refreshing GitLab.", ex);
            this.GITLAB = null;
        }
        try {
            Assertions.assertNotNull(this.GITGRIEFED.refresh());
        } catch (Exception ex) {
            LOG.error("Error refreshing GitGriefed.", ex);
            this.GITGRIEFED = null;
        }

        if (GITHUB != null) {
            Assertions.assertNotNull(GITHUB.latestVersion(false));
            Assertions.assertNotNull(GITHUB.latestVersion(true));
            Assertions.assertNotNull(GITHUB.latestAlpha());
            Assertions.assertNotNull(GITHUB.latestBeta());
            GITHUB.allVersions();
            Assertions.assertNotNull(GITHUB.getDownloadUrl("2.1.1"));
            Assertions.assertNotNull(GITHUB.getAssetsDownloadUrls("2.1.1"));
        }
        if (GITLAB != null) {
            Assertions.assertNotNull(GITLAB.latestVersion(false));
            Assertions.assertNotNull(GITLAB.latestVersion(true));
            Assertions.assertNotNull(GITLAB.latestAlpha());
            Assertions.assertNotNull(GITLAB.latestBeta());
            Assertions.assertNotNull(GITLAB.getDownloadUrl("2.1.1"));
            Assertions.assertNotNull(GITLAB.getAssetsDownloadUrls("2.1.1"));
        }
        if (GITGRIEFED != null) {
            Assertions.assertNotNull(GITGRIEFED.latestVersion(false));
            Assertions.assertNotNull(GITGRIEFED.latestVersion(true));
            Assertions.assertNotNull(GITGRIEFED.latestAlpha());
            Assertions.assertNotNull(GITGRIEFED.latestBeta());
            Assertions.assertNotNull(GITGRIEFED.getDownloadUrl("2.1.1"));
            Assertions.assertNotNull(GITGRIEFED.getAssetsDownloadUrls("2.1.1"));
        }

        String regular = checkForUpdateRegularRelease();
        String alpha = checkForUpdatePreReleaseAlpha();
        String beta = checkForUpdatePreReleaseBeta();
        String oldAlpha = checkForUpdatePreReleaseOldAlpha();
        String oldBeta = checkForUpdatePreReleaseOldBeta();
        String future = checkForUpdateFutureVersion();
        String futurePre = checkForUpdateFutureVersionPre();

        Assertions.assertNotNull(regular);

        Assertions.assertNotNull(alpha);

        Assertions.assertNotNull(beta);

        Assertions.assertNotNull(oldAlpha);

        Assertions.assertNotNull(oldBeta);

        Assertions.assertEquals("No updates available.",future);

        Assertions.assertEquals("No updates available.",futurePre);

        System.out.println("regular: " + regular);
        System.out.println("alpha " + alpha);
        System.out.println("beta " + beta);
        System.out.println("old alpha " + oldAlpha);
        System.out.println("old beta " + oldBeta);
        System.out.println("future: " + future);
        System.out.println("futurePre: " + futurePre);
    }

    public String checkForUpdateRegularRelease() {
        String currentVersion = "2.1.1";

        String updater = null;

        // Check GitHub for the most recent release.
        if (GITHUB != null) {

            // Check GitHub for new versions which are not pre-releases. Run with true to check pre-releases as well.
            updater = GITHUB.checkForUpdate(currentVersion, false);
        }


        if (GITGRIEFED != null && updater != null) {

            // After checking GitLab, and we did not get a version, check GitGriefed.
            // Check GitGriefed for new versions which are not pre-releases. Run with true to check pre-releases as well.
            // Only check if we did not already get a version from prior checks.
            if (!updater.contains(";") && GITGRIEFED.checkForUpdate(currentVersion, false).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], false);

                // Check GitGriefed for a newer version, with the version received from GitHub, if we received a new version from GitHub.
                // Don't check for pre-releases.
            } else if (updater.contains(";") && GITGRIEFED.checkForUpdate(updater.split(";")[0], false).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], false);
            }
        }


        if (GITLAB != null && updater != null) {

            // After checking GitGriefed, and we did not get a version, check GitLab.
            // Check GitLab for new versions which are not pre-releases. Run with true to check pre-releases as well.
            // Only check if we did not already get a version from prior checks.
            if (!updater.contains(";") && GITLAB.checkForUpdate(currentVersion, false).contains(";")) {
                updater = GITLAB.checkForUpdate(updater.split(";")[0], false);

                // Check GitLab for a newer version, with the version we received from GitGriefed, if we received a new version from GitGriefed.
                // Don't check for pre-releases.
            } else if (updater.contains(";") && GITLAB.checkForUpdate(updater.split(";")[0], false).contains(";")) {
                updater = GITLAB.checkForUpdate(updater.split(";")[0], false);
            }
        }

        // Output can be either "No updates available." if...well...no updates are available.
        // or "2.1.1;https://github.com/Griefed/ServerPackCreator/releases/download/2.1.1/serverpackcreator-2.1.1.jar"
        // if you ran this for ServerPackCreator, with version 2.1.1, without checking for pre-releases. (at the time of me writing this)

        LOG.info("Update checks returned: " + updater);

        return updater;
    }

    public String checkForUpdatePreReleaseAlpha() {
        String currentVersion = "3.0.0-alpha.2";

        String updater = null;

        // Check GitHub for the most recent release.
        if (GITHUB != null) {

            // Check GitHub for new versions which are not pre-releases. Run with true to check pre-releases as well.
            updater = GITHUB.checkForUpdate(currentVersion, true);
        }


        if (GITGRIEFED != null && updater != null) {

            // After checking GitLab, and we did not get a version, check GitGriefed.
            // Check GitGriefed for new versions which are not pre-releases. Run with true to check pre-releases as well.
            // Only check if we did not already get a version from prior checks.
            if (!updater.contains(";") && GITGRIEFED.checkForUpdate(currentVersion, true).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], true);

                // Check GitGriefed for a newer version, with the version received from GitHub, if we received a new version from GitHub.
                // Don't check for pre-releases.
            } else if (updater.contains(";") && GITGRIEFED.checkForUpdate(updater.split(";")[0], true).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], true);
            }
        }


        if (GITLAB != null && updater != null) {

            // After checking GitGriefed, and we did not get a version, check GitLab.
            // Check GitLab for new versions which are not pre-releases. Run with true to check pre-releases as well.
            // Only check if we did not already get a version from prior checks.
            if (!updater.contains(";") && GITLAB.checkForUpdate(currentVersion, true).contains(";")) {
                updater = GITLAB.checkForUpdate(updater.split(";")[0], true);

                // Check GitLab for a newer version, with the version we received from GitGriefed, if we received a new version from GitGriefed.
                // Don't check for pre-releases.
            } else if (updater.contains(";") && GITLAB.checkForUpdate(updater.split(";")[0], true).contains(";")) {
                updater = GITLAB.checkForUpdate(updater.split(";")[0], true);
            }
        }

        // Output can be either "No updates available." if...well...no updates are available.
        // or "2.1.1;https://github.com/Griefed/ServerPackCreator/releases/download/2.1.1/serverpackcreator-2.1.1.jar"
        // if you ran this for ServerPackCreator, with version 2.1.1, without checking for pre-releases. (at the time of me writing this)

        LOG.info("Update checks returned: " + updater);

        return updater;
    }

    public String checkForUpdatePreReleaseBeta() {
        String currentVersion = "3.0.0-beta.2";

        String updater = null;

        if (GITHUB != null) {

            updater = GITHUB.checkForUpdate(currentVersion, true);
        }


        if (GITGRIEFED != null && updater != null) {

            if (!updater.contains(";") && GITGRIEFED.checkForUpdate(currentVersion, true).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], true);

            } else if (updater.contains(";") && GITGRIEFED.checkForUpdate(updater.split(";")[0], true).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], true);
            }
        }


        if (GITLAB != null && updater != null) {

            if (!updater.contains(";") && GITLAB.checkForUpdate(currentVersion, true).contains(";")) {
                updater = GITLAB.checkForUpdate(updater.split(";")[0], true);

            } else if (updater.contains(";") && GITLAB.checkForUpdate(updater.split(";")[0], true).contains(";")) {
                updater = GITLAB.checkForUpdate(updater.split(";")[0], true);
            }
        }

        LOG.info("Update checks returned: " + updater);

        return updater;
    }

    private String checkForUpdatePreReleaseOldAlpha() {
        String currentVersion = "2.0.0-alpha.2";

        String updater = null;

        if (GITHUB != null) {

            updater = GITHUB.checkForUpdate(currentVersion, true);
        }


        if (GITGRIEFED != null && updater != null) {

            if (!updater.contains(";") && GITGRIEFED.checkForUpdate(currentVersion, true).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], true);

            } else if (updater.contains(";") && GITGRIEFED.checkForUpdate(updater.split(";")[0], true).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], true);
            }
        }


        if (GITLAB != null && updater != null) {

            if (!updater.contains(";") && GITLAB.checkForUpdate(currentVersion, true).contains(";")) {
                updater = GITLAB.checkForUpdate(updater.split(";")[0], true);

            } else if (updater.contains(";") && GITLAB.checkForUpdate(updater.split(";")[0], true).contains(";")) {
                updater = GITLAB.checkForUpdate(updater.split(";")[0], true);
            }
        }

        LOG.info("Update checks returned: " + updater);

        return updater;
    }

    private String checkForUpdatePreReleaseOldBeta() {
        String currentVersion = "2.0.0-beta.2";

        String updater = null;

        if (GITHUB != null) {

            updater = GITHUB.checkForUpdate(currentVersion, true);
        }


        if (GITGRIEFED != null && updater != null) {

            if (!updater.contains(";") && GITGRIEFED.checkForUpdate(currentVersion, true).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], true);

            } else if (updater.contains(";") && GITGRIEFED.checkForUpdate(updater.split(";")[0], true).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], true);
            }
        }


        if (GITLAB != null && updater != null) {

            if (!updater.contains(";") && GITLAB.checkForUpdate(currentVersion, true).contains(";")) {
                updater = GITLAB.checkForUpdate(updater.split(";")[0], true);

            } else if (updater.contains(";") && GITLAB.checkForUpdate(updater.split(";")[0], true).contains(";")) {
                updater = GITLAB.checkForUpdate(updater.split(";")[0], true);
            }
        }

        LOG.info("Update checks returned: " + updater);

        return updater;
    }

    private String checkForUpdateFutureVersion() {
        String currentVersion = "123.456.789";

        String updater = null;

        if (GITHUB != null) {

            updater = GITHUB.checkForUpdate(currentVersion, false);
        }


        if (GITGRIEFED != null && updater != null) {

            if (!updater.contains(";") && GITGRIEFED.checkForUpdate(currentVersion, false).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], false);

            } else if (updater.contains(";") && GITGRIEFED.checkForUpdate(updater.split(";")[0], false).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], false);
            }
        }


        if (GITLAB != null && updater != null) {

            if (!updater.contains(";") && GITLAB.checkForUpdate(currentVersion, false).contains(";")) {
                updater = GITLAB.checkForUpdate(updater.split(";")[0], false);

            } else if (updater.contains(";") && GITLAB.checkForUpdate(updater.split(";")[0], false).contains(";")) {
                updater = GITLAB.checkForUpdate(updater.split(";")[0], false);
            }
        }

        LOG.info("Update checks returned: " + updater);

        return updater;
    }

    private String checkForUpdateFutureVersionPre() {
        String currentVersion = "123.456.789";

        String updater = null;

        if (GITHUB != null) {

            updater = GITHUB.checkForUpdate(currentVersion, true);
        }


        if (GITGRIEFED != null && updater != null) {

            if (!updater.contains(";") && GITGRIEFED.checkForUpdate(currentVersion, true).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], true);

            } else if (updater.contains(";") && GITGRIEFED.checkForUpdate(updater.split(";")[0], true).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], true);
            }
        }


        if (GITLAB != null && updater != null) {

            if (!updater.contains(";") && GITLAB.checkForUpdate(currentVersion, true).contains(";")) {
                updater = GITLAB.checkForUpdate(updater.split(";")[0], true);

            } else if (updater.contains(";") && GITLAB.checkForUpdate(updater.split(";")[0], true).contains(";")) {
                updater = GITLAB.checkForUpdate(updater.split(";")[0], true);
            }
        }

        LOG.info("Update checks returned: " + updater);

        return updater;
    }
}
