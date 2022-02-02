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
        refresh();
        Assertions.assertEquals("No updates available.",checkForUpdate());
    }

    public UpdateCheckerTests refresh() {
        try {
            this.GITHUB.refresh();
        } catch (Exception ex) {
            LOG.error("Error refreshing GitHub.", ex);
            this.GITHUB = null;
        }
        try {
            this.GITLAB.refresh();
        } catch (Exception ex) {
            LOG.error("Error refreshing GitLab.", ex);
            this.GITLAB = null;
        }
        try {
            this.GITGRIEFED.refresh();
        } catch (Exception ex) {
            LOG.error("Error refreshing GitGriefed.", ex);
            this.GITGRIEFED = null;
        }
        return this;
    }

    public String checkForUpdate() {
        String updater = "No updates available.";

        // Check GitHub for the most recent release.
        if (GITHUB != null) {

            // Check GitHub for new versions which are not pre-releases. Run with true to check pre-releases as well.
            updater = GITHUB.checkForUpdate("2.1.1", false);
        }


        if (GITGRIEFED != null) {

            // After checking GitLab, and we did not get a version, check GitGriefed.
            // Check GitGriefed for new versions which are not pre-releases. Run with true to check pre-releases as well.
            // Only check if we did not already get a version from prior checks.
            if (!updater.contains(";") && GITGRIEFED.checkForUpdate("your current version here", false).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], false);

                // Check GitGriefed for a newer version, with the version received from GitHub, if we received a new version from GitHub.
                // Don't check for pre-releases.
            } else if (updater.contains(";") && GITGRIEFED.checkForUpdate(updater.split(";")[0], false).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], false);
            }
        }


        if (GITLAB != null) {

            // After checking GitGriefed, and we did not get a version, check GitLab.
            // Check GitLab for new versions which are not pre-releases. Run with true to check pre-releases as well.
            // Only check if we did not already get a version from prior checks.
            if (!updater.contains(";") && GITLAB.checkForUpdate("your current version here", false).contains(";")) {
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
}
