## [1.1.0](https://git.griefed.de/Griefed/VersionChecker/compare/1.0.8...1.1.0) (2022-04-17)


### 📔 Docs

* **Examples:** Provide an example for the usage of the new Update-object in the README and expand sourceTar and sourceTarBz2 methods descriptions on when they are available. ([9af7137](https://git.griefed.de/Griefed/VersionChecker/commit/9af7137a16eeed543795426c2825867bf400e398))


### 🚀 Features

* **Update acquisition:** Acquire an instance of Update via `check(...)` which contains information about the available update, such as the version, description, sources, assets(if any), release date and url to the release. ([d20f1c7](https://git.griefed.de/Griefed/VersionChecker/commit/d20f1c7ef362222a5d061a47e0a12f278ad2f160))


### Other

* **deps:** update dependency com.fasterxml.jackson.core:jackson-databind to v2.13.2.2 ([93e475d](https://git.griefed.de/Griefed/VersionChecker/commit/93e475d5b18114c42c452e417ed1c270cad9ead6))
* **deps:** update dependency ghcr.io/griefed/baseimage-ubuntu-jdk-8 to v2.0.8 ([cc83427](https://git.griefed.de/Griefed/VersionChecker/commit/cc83427d8a7e618e5e282cdd081d259724ae8f75))
* **deps:** update dependency ghcr.io/griefed/gitlab-ci-cd to v2.0.5 ([92e8fcc](https://git.griefed.de/Griefed/VersionChecker/commit/92e8fcc0a8772b5af985b44568f8f08c889745e7))
* **deps:** update dependency gradle to v7.4.2 ([09c1c5a](https://git.griefed.de/Griefed/VersionChecker/commit/09c1c5ad3be80b9b945b41a0655eabe965c91931))

### [1.0.8](https://git.griefed.de/Griefed/VersionChecker/compare/1.0.7...1.0.8) (2022-03-23)


### 🦊 CI/CD

* **GitHub:** Fix release asset path ([009c4d2](https://git.griefed.de/Griefed/VersionChecker/commit/009c4d24cbefe9787f300130e0fc935704884465))


### 🛠 Fixes

* When an alpha or beta for the latest release is used, return the latest release as the available update. Example: 1.2.5-alpha.1 -> 1.2.5 and 1.2.5-beta.1 -> 1.2.5 ([45a6296](https://git.griefed.de/Griefed/VersionChecker/commit/45a6296b68d5df3c98cf6e362306543bf3fc3d98))


### Other

* **deps:** update actions/setup-java action to v3 ([47235d4](https://git.griefed.de/Griefed/VersionChecker/commit/47235d483f2a4e61588c9821c907121b29262614))
* **deps:** update dependency ghcr.io/griefed/baseimage-ubuntu-jdk-8 to v2.0.7 ([74de493](https://git.griefed.de/Griefed/VersionChecker/commit/74de49380198a9c4c9c011aaba89730d7c8ac996))
* **deps:** update dependency ghcr.io/griefed/gitlab-ci-cd to v2.0.4 ([4cde6ba](https://git.griefed.de/Griefed/VersionChecker/commit/4cde6ba496d38f460e633e8ef899ebee1137f0be))
* **deps:** update dependency gradle to v7.4 ([97b1ff0](https://git.griefed.de/Griefed/VersionChecker/commit/97b1ff004b4a97e20dec516cfd0586dd24ddb7e8))
* **deps:** update dependency griefed/baseimage-ubuntu-jdk-8 to v2.0.6 ([e8f363b](https://git.griefed.de/Griefed/VersionChecker/commit/e8f363b75fe62baa3825709e98ac5523812fe374))
* **deps:** update dependency griefed/gitlab-ci-cd to v2.0.3 ([94d7d29](https://git.griefed.de/Griefed/VersionChecker/commit/94d7d29b44285073039bc18d27c5aef0ba5341df))
* **deps:** update dependency org.jetbrains:annotations to v23 ([4254cab](https://git.griefed.de/Griefed/VersionChecker/commit/4254cab598455dd1ee8be64ae08a307b13ff9657))
* **deps:** update jamesives/github-pages-deploy-action action to v4.2.5 ([413eeb2](https://git.griefed.de/Griefed/VersionChecker/commit/413eeb2256d6772a0fe44b6218dbc6ab2c620b22))

### [1.0.7](https://git.griefed.de/Griefed/VersionChecker/compare/1.0.6...1.0.7) (2022-02-26)


### 🦊 CI/CD

* **GitHub:** Fix paths for pre-release assets. Automatically generate changelog from git history. ([6ef66d9](https://git.griefed.de/Griefed/VersionChecker/commit/6ef66d93b090767d05ddccb3b5ab524ab23de33b))
* **GitHub:** Fix paths for release assets. Automatically generate changelog from git history. ([c1fc6bd](https://git.griefed.de/Griefed/VersionChecker/commit/c1fc6bdd8629184c7811306953ae06c0a3d2f947))
* **GitHub:** Remove artifacts which aren't being produced anyway ([2b8505a](https://git.griefed.de/Griefed/VersionChecker/commit/2b8505ad67fb56599ef145868a2dab9ddd1c24ae))


### 🛠 Fixes

* Fix regression bug introduced during last fix. Correctly acquire latest version from repository and correctly compare against other versions, preventing false positives and premature availability messages. ([6cfcecf](https://git.griefed.de/Griefed/VersionChecker/commit/6cfcecf2d7936cc6dd515dcb2d8839b29ea04286))


### Other

* **deps:** update dependency ghcr.io/griefed/baseimage-ubuntu-jdk-8 to v2.0.6 ([f903116](https://git.griefed.de/Griefed/VersionChecker/commit/f90311660e53e839cd88a366e35da8fcf7e75f88))
* **deps:** update dependency ghcr.io/griefed/gitlab-ci-cd to v2.0.3 ([b560503](https://git.griefed.de/Griefed/VersionChecker/commit/b5605032a9669f4f70fdfcac0d5712ac58894667))

### [1.0.6](https://git.griefed.de/Griefed/VersionChecker/compare/1.0.5...1.0.6) (2022-02-24)


### 🦊 CI/CD

* Enable RenovateBot dependency updates ([dc8b030](https://git.griefed.de/Griefed/VersionChecker/commit/dc8b03088fb82c074050c9c00e72b90bf2f913e6))
* **GitHub:** Correctly execute (pre)release actions when tags are pushed. ([ae605c0](https://git.griefed.de/Griefed/VersionChecker/commit/ae605c07a5dbf8d21c15047b834845c91a787020))


### 🛠 Fixes

* Get rid of substring calls. Check a given release, alpha, or beta version for available updates and ensure checks are performed correctly. ([345bf8d](https://git.griefed.de/Griefed/VersionChecker/commit/345bf8d8005bc842941d8bc8419f6e38df409fa6))


### Other

* Cleanups ([7727715](https://git.griefed.de/Griefed/VersionChecker/commit/77277151d33a216f742e1e0265a9f15ab2151a1f))

### [1.0.5](https://git.griefed.de/Griefed/VersionChecker/compare/1.0.4...1.0.5) (2022-02-04)


### 📔 Docs

* Improve documentation ([fffe5e8](https://git.griefed.de/Griefed/VersionChecker/commit/fffe5e8f085ea05c4a2189119034dc695edd4553))


### 🧪 Tests

* Some more assertions for versions and URLs ([e3a5a8a](https://git.griefed.de/Griefed/VersionChecker/commit/e3a5a8a7c43a68ae998444732710f30106f7f340))

### [1.0.4](https://git.griefed.de/Griefed/VersionChecker/compare/1.0.3...1.0.4) (2022-02-03)


### 🛠 Fixes

* Running out of ideas ([4156706](https://git.griefed.de/Griefed/VersionChecker/commit/415670698de6adf5635207314263f47a0ed8adbb))

### [1.0.3](https://git.griefed.de/Griefed/VersionChecker/compare/1.0.2...1.0.3) (2022-02-03)


### 🛠 Fixes

* Hopefully fix OSSRH username and password retrieval... Fix GitHub actions webhooks ([d2ae043](https://git.griefed.de/Griefed/VersionChecker/commit/d2ae0439af33d2db9823a351a7bdc8d9054492fd))

### [1.0.2](https://git.griefed.de/Griefed/VersionChecker/compare/1.0.1...1.0.2) (2022-02-03)


### 🛠 Fixes

* Remove another failing echo. Retrieve OSSRH properties correctly. ([f8e21e6](https://git.griefed.de/Griefed/VersionChecker/commit/f8e21e6c1f448c49ca58837ee8578a8c542ac64a))

### [1.0.1](https://git.griefed.de/Griefed/VersionChecker/compare/1.0.0...1.0.1) (2022-02-03)


### 🛠 Fixes

* Remove failing echo. Refactor urls in build.gradle. Clean environment before publishing ([a30f874](https://git.griefed.de/Griefed/VersionChecker/commit/a30f874e122891f65b8511c28fa240fd4a3a541f))

## [1.0.0](https://git.griefed.de/Griefed/VersionChecker/compare/...1.0.0) (2022-02-03)


### 🧨 Breaking changes!

* Overhaul everything. Allow to check for pre- and regular releases. Throw exceptions at correct places. Improve tests with realistic example. ([4e9d91f](https://git.griefed.de/Griefed/VersionChecker/commit/4e9d91f00355a965b52f590e587f1ac8b43d302d))


### Other

* Add information regarding implementation ([5e2bdf4](https://git.griefed.de/Griefed/VersionChecker/commit/5e2bdf4195dfc0278bba4c3e9e901ceb27900ee7))
