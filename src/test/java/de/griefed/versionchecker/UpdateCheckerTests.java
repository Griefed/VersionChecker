package de.griefed.versionchecker;

import de.griefed.versionchecker.github.GitHubChecker;
import de.griefed.versionchecker.gitlab.GitLabChecker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.IOException;

@SuppressWarnings("BusyWait")
public class UpdateCheckerTests {

    private static final Logger LOG = LogManager.getLogger(UpdateCheckerTests.class);

    private GitHubChecker GITHUB;
    private GitLabChecker GITGRIEFED;
    private GitLabChecker GITLAB;

    //private GitHubChecker tests;

    public UpdateCheckerTests() throws IOException {

        this.GITHUB = new GitHubChecker("Griefed/ServerPackCreator");
        this.GITLAB = new GitLabChecker("https://gitlab.com/api/v4/projects/32677538/releases");
        this.GITGRIEFED = new GitLabChecker("https://git.griefed.de/api/v4/projects/63/releases");

        //this.tests = new GitHubChecker("Griefed/Action_tests");
        //this.tests.refresh();
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

        if (GITHUB == null) {
            do {
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    Assertions.assertNotNull(this.GITHUB.refresh());
                } catch (Exception ex) {
                    LOG.error("Error refreshing GitHub.", ex);
                    this.GITHUB = null;
                }
            } while (GITHUB == null);
        }

        if (GITLAB == null) {
            do {
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    Assertions.assertNotNull(this.GITLAB.refresh());
                } catch (Exception ex) {
                    LOG.error("Error refreshing GitLab.", ex);
                    this.GITLAB = null;
                }
            } while (GITLAB == null);
        }

        if (GITGRIEFED == null) {
            do {
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    Assertions.assertNotNull(this.GITGRIEFED.refresh());
                } catch (Exception ex) {
                    LOG.error("Error refreshing GitGriefed.", ex);
                    this.GITGRIEFED = null;
                }
            } while (GITGRIEFED == null);
        }

        Assertions.assertNotNull(GITHUB.latestVersion(false));
        Assertions.assertNotNull(GITHUB.latestVersion(true));
        Assertions.assertNotNull(GITHUB.latestAlpha());
        Assertions.assertNotNull(GITHUB.latestBeta());
        GITHUB.allVersions();
        Assertions.assertNotNull(GITHUB.getDownloadUrl("2.1.1"));
        Assertions.assertNotNull(GITHUB.getAssetsDownloadUrls("2.1.1"));

        Assertions.assertNotNull(GITLAB.latestVersion(false));
        Assertions.assertNotNull(GITLAB.latestVersion(true));
        Assertions.assertNotNull(GITLAB.latestAlpha());
        Assertions.assertNotNull(GITLAB.latestBeta());
        Assertions.assertNotNull(GITLAB.getDownloadUrl("2.1.1"));
        Assertions.assertNotNull(GITLAB.getAssetsDownloadUrls("2.1.1"));

        Assertions.assertNotNull(GITGRIEFED.latestVersion(false));
        Assertions.assertNotNull(GITGRIEFED.latestVersion(true));
        Assertions.assertNotNull(GITGRIEFED.latestAlpha());
        Assertions.assertNotNull(GITGRIEFED.latestBeta());
        Assertions.assertNotNull(GITGRIEFED.getDownloadUrl("2.1.1"));
        Assertions.assertNotNull(GITGRIEFED.getAssetsDownloadUrls("2.1.1"));

        String latest = GITHUB.latestVersion(false);
        String latestPre = GITHUB.latestVersion(true);

        System.out.println("Old version should return the newest regular release, whilst not checking for pre-releases.");
        Assertions.assertEquals(
                latest,
                checkForUpdate(
                        "2.0.0",
                        false
        ).split(";")[0]);

        System.out.println("Old version should return the newest pre-release release, whilst checking for pre-releases, too.");
        Assertions.assertEquals(
                latestPre,
                checkForUpdate(
                        "2.0.0",
                        true
        ).split(";")[0]);



        System.out.println("Old alpha should return the newest regular release, whilst not checking for pre-releases.");
        Assertions.assertEquals(
                latest,
                checkForUpdate(
                        "2.0.0-alpha.2",
                        false
        ).split(";")[0]);

        System.out.println("Old alpha should return the newest alpha/beta, whilst checking for pre-releases, too.");
        Assertions.assertEquals(
                latestPre,
                checkForUpdate(
                        "2.0.0-alpha.2",
                        true
        ).split(";")[0]);



        System.out.println("Old beta should return the newest regular release, whilst not checking for pre-releases.");
        Assertions.assertEquals(
                latest,
                checkForUpdate(
                        "2.0.0-beta.2",
                        false
        ).split(";")[0]);

        System.out.println("Old beta should return the newest alpha/beta, whilst checking for pre-releases, too.");
        Assertions.assertEquals(
                latestPre,
                checkForUpdate(
                        "2.0.0-beta.2",
                        true
        ).split(";")[0]);



        System.out.println("Old beta, but newer than the newest regular release, should return no available updates, whilst not checking for pre-releases.");
        Assertions.assertEquals(
                "No updates available.",
                checkForUpdate(
                        "3.0.0-beta.2",
                        false
                )
        );

        System.out.println("Old beta, but newer than the newest regular release, should the latest available alpha/beta, whilst checking for pre-releases, too.");
        Assertions.assertEquals(
                latestPre,
                checkForUpdate(
                        "3.0.0-beta.2",
                        true
        ).split(";")[0]);



        System.out.println("Old beta, but newer than the newest regular release, should return no available updates, whilst not checking for pre-releases.");
        Assertions.assertEquals(
                "No updates available.",
                checkForUpdate(
                        "3.0.0-alpha.2",
                        false
                )
        );

        System.out.println("Old alpha, but newer than the newest regular release, should return the newest alpha/beta, whilst checking for pre-releases, too.");
        Assertions.assertEquals(
                latestPre,
                checkForUpdate(
                        "3.0.0-alpha.2",
                        true
        ).split(";")[0]);



        System.out.println("Future alpha should return no available updates, whilst not checking for pre-releases.");
        Assertions.assertEquals(
                "No updates available.",
                checkForUpdate(
                        "123.456.789-alpha.2",
                        false
                )
        );

        System.out.println("Future alpha should return no available updates, whilst checking for pre-releases, too.");
        Assertions.assertEquals(
                "No updates available.",
                checkForUpdate(
                        "123.456.789-alpha.2",
                        true
                )
        );



        System.out.println("Future beta should return no available updates, whilst not checking for pre-releases.");
        Assertions.assertEquals(
                "No updates available.",
                checkForUpdate(
                        "123.456.789-beta.2",
                        false
                )
        );

        System.out.println("Future beta should return no available updates, whilst checking for pre-releases, too.");
        Assertions.assertEquals(
                "No updates available.",
                checkForUpdate(
                        "123.456.789-beta.2",
                        true
                )
        );



        System.out.println("Newer version should return no available updates, whilst not checking for pre-releases.");
        Assertions.assertEquals(
                "No updates available.",
                checkForUpdate(
                        "123.456.789",
                        false
                )
        );

        System.out.println("Newer version should return no available updates, whilst checking for pre-releases.");
        Assertions.assertEquals(
                "No updates available.",
                checkForUpdate(
                        "123.456.789",
                        true
                )
        );



        System.out.println("Latest version should return no available updates, whilst not checking for pre-releases.");
        Assertions.assertEquals(
                "No updates available.",
                checkForUpdate(
                        latest,
                        false
                )
        );

        System.out.println("Latest alpha/beta should return no available updates, whilst checking for pre-releases, too.");
        Assertions.assertEquals(
                "No updates available.",
                checkForUpdate(
                        latestPre,
                        true
                )
        );



        System.out.println("Beta for latest version should return latest version as available update, whilst not checking for pre-releases.");
        Assertions.assertEquals(
                latest,
                checkForUpdate(
                        latest + "-beta.2",
                        false
        ).split(";")[0]);

        System.out.println("Beta for latest version should return latest version as available update.");
        Assertions.assertEquals(
                latestPre,
                checkForUpdate(
                        latest + "-beta.2",
                        true
        ).split(";")[0]);



        System.out.println("Alpha for latest version should return latest version as available update.");
        Assertions.assertEquals(
                latest,
                checkForUpdate(
                        latest + "-alpha.12",
                        false
        ).split(";")[0]);

        System.out.println("Alpha for latest version should return latest version as available update.");
        Assertions.assertEquals(
                latestPre,
                checkForUpdate(
                        latest + "-alpha.2",
                        true
        ).split(";")[0]);


        /*System.out.println(tests.latestVersion(false));
        System.out.println("Beta for latest version should return latest version as available update, whilst not checking for pre-releases.");
        Assertions.assertEquals(
                "1.2.5",
                testChecker(
                        "1.2.5-beta.1",
                        false
                ).split(";")[0]);

        System.out.println("Beta for latest version should return latest version as available update.");
        Assertions.assertEquals(
                "1.2.5",
                testChecker(
                        "1.2.5-beta.1",
                        true
                ).split(";")[0]);



        System.out.println("Alpha for latest version should return latest version as available update.");
        Assertions.assertEquals(
                "1.2.5",
                testChecker(
                        "1.2.5-alpha.1",
                        false
                ).split(";")[0]);

        System.out.println("Alpha for latest version should return latest version as available update.");
        Assertions.assertEquals(
                "1.2.5",
                testChecker(
                        "1.2.5-alpha.1",
                        true
                ).split(";")[0]);*/
    }

    /*private String testChecker(String version, boolean pre) {
        String updater = null;

        // Check GitHub for the most recent release.
        if (tests != null) {

            // Check GitHub for new versions which are not pre-releases. Run with true to check pre-releases as well.
            updater = tests.checkForUpdate(version, pre);
        }

        System.out.println("Check pre:       " + pre    );
        System.out.println("Checked version: " + version);
        System.out.println("Result:          " + updater);
        System.out.println();

        return updater;
    }*/

    private String checkForUpdate(String version, boolean pre) {

        String updater = null;

        // Check GitHub for the most recent release.
        if (GITHUB != null) {

            // Check GitHub for new versions which are not pre-releases. Run with true to check pre-releases as well.
            updater = GITHUB.checkForUpdate(version, pre);
        }


        if (GITGRIEFED != null && updater != null) {

            // After checking GitLab, and we did not get a version, check GitGriefed.
            // Check GitGriefed for new versions which are not pre-releases. Run with true to check pre-releases as well.
            // Only check if we did not already get a version from prior checks.
            if (!updater.contains(";") && GITGRIEFED.checkForUpdate(version, pre).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(version, pre);

                // Check GitGriefed for a newer version, with the version received from GitHub, if we received a new version from GitHub.
                // Don't check for pre-releases.
            } else if (updater.contains(";") && GITGRIEFED.checkForUpdate(updater.split(";")[0], pre).contains(";")) {
                updater = GITGRIEFED.checkForUpdate(updater.split(";")[0], pre);
            }
        }


        if (GITLAB != null && updater != null) {

            // After checking GitGriefed, and we did not get a version, check GitLab.
            // Check GitLab for new versions which are not pre-releases. Run with true to check pre-releases as well.
            // Only check if we did not already get a version from prior checks.
            if (!updater.contains(";") && GITLAB.checkForUpdate(version, pre).contains(";")) {
                updater = GITLAB.checkForUpdate(version, pre);

                // Check GitLab for a newer version, with the version we received from GitGriefed, if we received a new version from GitGriefed.
                // Don't check for pre-releases.
            } else if (updater.contains(";") && GITLAB.checkForUpdate(updater.split(";")[0], pre).contains(";")) {
                updater = GITLAB.checkForUpdate(updater.split(";")[0], pre);
            }
        }

        // Output can be either "No updates available." if...well...no updates are available.
        // or "2.1.1;https://github.com/Griefed/ServerPackCreator/releases/download/2.1.1/serverpackcreator-2.1.1.jar"
        // if you ran this for ServerPackCreator, with version 2.1.1, without checking for pre-releases. (at the time of me writing this)

        System.out.println("Check pre:       " + pre    );
        System.out.println("Checked version: " + version);
        System.out.println("Result:          " + updater);
        System.out.println();

        return updater;
    }
}
