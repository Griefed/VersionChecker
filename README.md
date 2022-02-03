# Simple semantic version checker for GitHub and GitLab

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

# Example

See [UpdateCheckerTests](src/test/java/de/griefed/versionchecker/UpdateCheckerTests.java)

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


