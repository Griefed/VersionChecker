# Simple semantic version checker for GitHub and GitLab

A simple version checker, using semantic-release formats like 1.2.3, 2.3.4 or 6.6.6, as well as 3.4.5-alpha.2 or 4.5.6-beta.3,
for a given GitHub user and repository, or GitLab instance and project ID.

Made for use as an update-checker in [ServerpackCreator](https://git.griefed.de/Griefed/ServerPackCreator), but you can do with it whatever you like.

# Usage

```java
// Create new instances of the GitHub and/or GitLab checkers.

// When using the GitHubChecker, make sure to instantiate with a valid GitHub username and repository.
GitHubChecker gitHubChecker = new GitHubChecker("Griefed", "ServerPackCreator");

//When using the GitLabChecker, make sure to instantiate with a valid GitLab instance-URL and repository ID.
GitLabChecker gitLabChecker = new GitLabChecker("https://git.griefed.de",63);

// The version of your app you want to check.
String currentVersion = "2.0.0";

// Check GitHub whether a new version is available, without checking for availability of alphas or betas-
System.out.println(gitHubChecker.checkForUpdate(currentVersion, false));

// Check GitHub whether a new version is available, with alhas and betas included. 
System.out.println(gitHubChecker.checkForUpdate(currentVersion, true));

// Check GitLab whether a new version is available, without checking for availability of alphas or betas-
System.out.println(gitLabChecker.checkForUpdate(currentVersion, false));

// Check GitLab whether a new version is available, with alhas and betas included. 
System.out.println(gitLabChecker.checkForUpdate(currentVersion, true));

// Download URLs for the assets of a particular release can also be retrieved from both GitHub and GitLab 
for (String url : gitHubChecker.getAssetsDownloadUrls("3.0.0-alpha.14")) {
    System.out.println(url);
}

for (String url : gitLabChecker.getAssetsDownloadUrls("3.0.0-alpha.14")) {
    System.out.println(url);
}
```

The example above would print the following (as of me writing this):

```shell
Current version: 2.0.0. A new release is available:2.1.1. Download available at: https://github.com/Griefed/ServerPackCreator/releases/tag/2.1.1
Current version: 2.0.0. A new PreRelease is available: 3.0.0-alpha.14. Download available at: https://github.com/Griefed/ServerPackCreator/releases/tag/3.0.0-alpha.14
Current version: 2.0.0. A new release is available:2.1.1. Download available at: https://git.griefed.de/Griefed/ServerPackCreator/-/releases/2.1.1
Current version: 2.0.0. A new PreRelease is available: 3.0.0-alpha.14. Download available at: https://git.griefed.de/Griefed/ServerPackCreator/-/releases/3.0.0-alpha.14
https://github.com/Griefed/ServerPackCreator/releases/download/3.0.0-alpha.14/ServerPackCreator-3.0.0-alpha.14-javadoc.jar
https://github.com/Griefed/ServerPackCreator/releases/download/3.0.0-alpha.14/ServerPackCreator-3.0.0-alpha.14-sources.jar
https://github.com/Griefed/ServerPackCreator/releases/download/3.0.0-alpha.14/ServerPackCreator-3.0.0-alpha.14.exe
https://github.com/Griefed/ServerPackCreator/releases/download/3.0.0-alpha.14/ServerPackCreator-3.0.0-alpha.14.jar
https://git.griefed.de/api/v4/projects/63/packages/generic/ServerPackCreator/3.0.0-alpha.14/ServerPackCreator-3.0.0-alpha.14-sources.jar
https://git.griefed.de/api/v4/projects/63/packages/generic/ServerPackCreator/3.0.0-alpha.14/ServerPackCreator-3.0.0-alpha.14-javadoc.jar
https://git.griefed.de/api/v4/projects/63/packages/generic/ServerPackCreator/3.0.0-alpha.14/ServerPackCreator-3.0.0-alpha.14.jar
https://git.griefed.de/api/v4/projects/63/packages/generic/ServerPackCreator/3.0.0-alpha.14/ServerPackCreator-3.0.0-alpha.14.exe
```

