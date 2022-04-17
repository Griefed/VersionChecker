# Simple semantic version checker for GitHub and GitLab

[![Homepage](https://img.shields.io/badge/Griefed.de-Homepage-c0ffee?style=for-the-badge&labelColor=325358&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAMAAACdt4HsAAACylBMVEUAAAD////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////9/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f39/f3+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v7+/v6OsnIvAAAA7XRSTlMAAQIDBAUGBwgJCgsMDQ4PEBESExQVFhcYGRobHB0eHyAhIiQlJicoKSorLC0uLzAxMjU2Nzg5Ojs8Pj9AQUJERUZHSElLTE9QUVJTVFVWV1hZXF1eX2BhYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ent8fX5/gIGCg4SGh4iJiouMjY6P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)](https://www.griefed.de)
[![Blog](https://img.shields.io/badge/Griefed.de-Blog-c0ffee?style=for-the-badge&labelColor=325358&logo=wordpress)](https://blog.griefed.de)
[![Fleet](https://img.shields.io/badge/Griefed.de-Fleet-c0ffee?style=for-the-badge&labelColor=325358&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAABiVBMVEUAAAD///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////8XJWL///8gNCTuAAAAgXRSTlMAAgMEBQYHCAkLDA4PEBESFhgZGh0eISIlKC8zNDU3OT0+P0BBQkhKS01QU1RVVltcXWJjZGlrbG1wcnN0eHl6e4CBhYyOkJWWmZ6foaKkqaqsrrKztLe8vsHExcbHy8zNz9DT1NXX2Nna293e3+Tm5+nq7O3u7/Hy9fb4+fr7/f4zgtRAAAABWklEQVR42mKgHhCFMbg0dHW1xNGlBcw5wTS7tkcJoNV5UJItBoMA3GPjcm3btm3btq1+8s1JxijuV0p+X5NzVcfNRoT4O26ClEZykdydI5ddCHDt+EfqthiwYoXfRH++yb9r8IZ+o/CpeB1hE/zMtXf0KYKkP9p8ZSaCfq1S2ddDU84zrsuXYXZjYy0VcBxQKYVmgQ9slS8HhUbxSP+kNAPB+kIyO1ggr5mn9GoX70wKydA4/RNQQSVPvGspZEHQ5ZKHK5lu8UymUi/enRQuxYjcN5LPH+RTQfcVlQ4AbfJ1n4JKRusG0ErpzCn3vjFMe8g9/eUU2himHEAilc+Vq6XJ4Zyx6eXTk3f6/IOww7i2oOliXNXQ/H5kHBcWSO2MowyKJc4VU/BLOGcMm04EJF3FyHsQImkvMj/tQBhrz0to+rwSUf70XvjT23VWxKLLaBkYGuqr+Y8f9Q3q0fzzGED8cgAAAABJRU5ErkJggg==)](https://fleet.griefed.de)
[![GitHub](https://img.shields.io/badge/Griefed.de-Github-c0ffee?style=for-the-badge&labelColor=325358&logo=github)](https://github.com/Griefed)
[![DockerHub](https://img.shields.io/badge/Griefed.de-DockerHub-c0ffee?style=for-the-badge&labelColor=325358&logo=docker&logoColor=white)](https://hub.docker.com/u/griefed)
[![Discord](https://img.shields.io/badge/Griefed.de-Discord-c0ffee?style=for-the-badge&labelColor=325358&logo=discord&logoColor=white)](https://discord.griefed.de)

---

# Sources, GitHub, GitLab and Mirroring and all that good stuff

Repositories on GitHub are now for issues only. I've set up my own installation of GitLab and moved all my repositories over to [Git.Griefed.de](https://git.griefed.de/users/Griefed/projects). Make sure to check there first for the latest code before opening an issue on GitHub.

For questions, you can always join my [Discord server](https://discord.griefed.de) and talk to me there.

###### This repository is available at:

- Source: https://git.griefed.de/Griefed/VersionChecker
- Mirror: https://gitlab.com/Griefed/VersionChecker
- Mirror: https://github.com/Griefed/VersionChecker
- Mirror: https://gitea.com/Griefed/VersionChecker

---

[![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/Griefed/VersionChecker?include_prereleases&label=Latest%20Release&logo=Github&style=for-the-badge&color=c0ffee&labelColor=325358)](https://github.com/Griefed/VersionChecker/releases/latest)
[![GitHub](https://img.shields.io/github/license/Griefed/VersionChecker?logo=GitHub&style=for-the-badge&color=c0ffee&labelColor=325358)](https://github.com/Griefed/VersionChecker/blob/main/LICENSE)

[![GitHub Repo stars](https://img.shields.io/github/stars/Griefed/VersionChecker?label=GitHub%20Stars&style=for-the-badge&logo=Github&labelColor=325358&color=c0ffee)](https://github.com/Griefed/VersionChecker)
[![GitHub forks](https://img.shields.io/github/forks/Griefed/VersionChecker?label=GitHub%20Forks&style=for-the-badge&logo=Github&labelColor=325358&color=c0ffee)](https://github.com/Griefed/VersionChecker)
[![GitHub contributors](https://img.shields.io/github/contributors/Griefed/VersionChecker?color=c0ffee&label=Contributors&logo=GitHub&logoColor=white&style=for-the-badge&labelColor=325358)](https://github.com/Griefed/VersionChecker/graphs/contributors)
[![GitHub all releases](https://img.shields.io/github/downloads/Griefed/VersionChecker/total?color=c0ffee&logo=GitHub&logoColor=white&labelColor=325358&style=for-the-badge)](https://github.com/Griefed/VersionChecker/releases)

[[_TOC_]]

A simple version checker, using semantic-release formats like 1.2.3, 2.3.4 or 6.6.6, as well as 3.4.5-alpha.2 or 4.5.6-beta.3,
for a given GitHub user and repository, or GitLab instance and project ID.

Made for use as an update-checker in [ServerpackCreator](https://git.griefed.de/Griefed/ServerPackCreator), but you can do with it whatever you like.

# Implementation

## Maven

```xml
<dependency>
  <groupId>de.griefed</groupId>
  <artifactId>versionchecker</artifactId>
  <version>$VERSION</version>
</dependency>
```

## Gradle

```groovy
implementation 'de.griefed:versionchecker:$VERSION'
```

### Versions

For available versions, see the [sonatype repo](https://search.maven.org/artifact/de.griefed/versionchecker/)

# Examples

See [UpdateCheckerTests](src/test/java/de/griefed/versionchecker/UpdateCheckerTests.java)

The simplest way to acquire update information is to call `.check(...)` of your GitHub- or GitHubChecker instances with your current version.
This will return an instance of [Update](https://github.com/Griefed/VersionChecker/blob/main/src/main/java/de/griefed/versionchecker/Update.java) which contains
details about the available update. It is wrapped in an Optional, so you can conveniently check via `.isPresent()` if an update is available.

Once an instance of [Update](https://github.com/Griefed/VersionChecker/blob/main/src/main/java/de/griefed/versionchecker/Update.java) has been retrieved,
you can access information about it via any of the available methods.

Example:

```java
GitHubChecker GITHUB = new GitHubChecker("Griefed/ServerPackCreator");

Optional<Update> gitHubUpdate = GITHUB.check("3.0.1",false);
if (gitHubUpdate.isPresent()) {
    
    // Gets the version of this update as a String.
    gitHubUpdate.get().version();
    
    // Check whether the update has a description
    if (gitHubUpdate.get().description().isPresent()) {
        
        // Get the description
        gitHubUpdate.get().description().get();
    }
    
    // Get the url to the release page of this update
    gitHubUpdate.get().url();
    
    // Get the date at which this update has been released.
    gitHubUpdate.get().releaseDate();
    
    // Check if the update has any assets attached to it
    if (gitHubUpdate.get().assets().isPresent()) {
        
        // Get the available assets of this update
        gitHubUpdate.get().assets().get();
    }
    
    // Get the available source-archives of this update
    gitHubUpdate.get().sources();
}

```

```java
public class UpdateChecker {

    private static final Logger LOG = LogManager.getLogger(UpdateChecker.class);

    private GitHubChecker GITHUB;
    private GitLabChecker GITGRIEFED;
    private GitLabChecker GITLAB;

    public UpdateChecker() throws IOException {

        this.GITHUB = new GitHubChecker("Griefed/ServerPackCreator");
        this.GITLAB = new GitLabChecker("https://gitlab.com/api/v4/projects/32677538/releases");
        this.GITGRIEFED = new GitLabChecker("https://git.griefed.de/api/v4/projects/63/releases");
    }

    public UpdateChecker refresh() {
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
            updater = GITHUB.checkForUpdate("your current version here", false);
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
```


