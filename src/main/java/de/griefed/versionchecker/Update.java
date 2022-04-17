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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * An instance of Update contains information about the release which is considered an update to the version which was used
 * to acquire this Update-instance.<br>
 * It gives you access to:<br>
 * - The version.<br>
 * - The description (release description as shown on release pages on GitLab or GitHub).<br>
 * - The {@link URL} to this release for visiting it in your browser.<br>
 * - The {@link LocalDate} at which this release was published/release.<br>
 * - A list of {@link ReleaseAsset}, if any.<br>
 * - A list of {@link Source}.
 * @author Griefed
 */
public class Update {

    private final String VERSION;
    private final String DESCRIPTION;
    private final URL LINK;
    private final LocalDate RELEASE_DATE;
    private final List<ReleaseAsset> ASSETS;
    private final List<Source> SOURCES;

    /**
     * Create a new Update-instance.
     * @author Griefed
     * @param version {@link String} The version of this update/release.
     * @param description {@link String} The description (release description as shown on release pages on GitLab or GitHub), of this release/update.
     * @param link {@link URL} The URL to this release for visiting it in your browser.
     * @param releaseDate {@link LocalDate} The date at which this release was published/release.
     * @param assets {@link ReleaseAsset}-list. Available release-assets for this update/release, if any.
     * @param sources {@link Source}-list. Available source-archives for this update/release.
     */
    public Update(@NotNull String version,
                  @Nullable String description,
                  @NotNull URL link,
                  @NotNull LocalDate releaseDate,
                  @Nullable List<ReleaseAsset> assets,
                  @NotNull List<Source> sources) {

        this.VERSION = version;
        this.DESCRIPTION = description;
        this.LINK = link;
        this.RELEASE_DATE = releaseDate;
        this.ASSETS = assets;
        this.SOURCES = sources;
    }

    /**
     * Get the version of this update/release.
     * @author Griefed
     * @return {@link String} The version of this update/release.
     */
    public String version() {
        return VERSION;
    }

    /**
     * Get the description of this update/release, wrapped in an {@link Optional}.
     * @author Griefed
     * @return {@link String} The description of this update/release, wrapped in an {@link Optional}.
     */
    public Optional<String> description() {
        return Optional.ofNullable(DESCRIPTION);
    }

    /**
     * Get the {@link URL} to this release for use in your browser.
     * @author Griefed
     * @return {@link URL} to this release for use in your browser.
     */
    public URL url() {
        return LINK;
    }

    /**
     * Get the {@link LocalDate} at which this release was published.
     * @author Griefed
     * @return {@link LocalDate} at which this release was published.
     */
    public LocalDate releaseDate() {
        return RELEASE_DATE;
    }

    /**
     * Get the {@link ReleaseAsset}-list of available assets for this update/release, wrapped in an {@link Optional}.
     * @author Griefed
     * @return {@link ReleaseAsset}-list of available assets for this update/release, wrapped in an {@link Optional}.
     */
    public Optional<List<ReleaseAsset>> assets() {
        return Optional.ofNullable(ASSETS);
    }

    /**
     * Get the {@link Source}-list of available source-archives for this update/release.
     * @author Griefed
     * @return {@link Source}-list of available source-archives for this update/release.
     */
    public List<Source> sources() {
        return SOURCES;
    }

    /**
     * Get the ZIP-archive-source of this update.
     * @author Griefed
     * @return {@link Source} of {@link ArchiveType#ZIP} of this update.
     */
    public Source sourceZip() {
        for (Source source : SOURCES) {
            if (source.type() == ArchiveType.ZIP) {
                return source;
            }
        }
        return null;
    }

    /**
     * Get the tar.gz-archive-source of this update.
     * @author Griefed
     * @return {@link Source} of {@link ArchiveType#TAR_GZ} of this update.
     */
    public Source sourceTarGz() {
        for (Source source : SOURCES) {
            if (source.type() == ArchiveType.TAR_GZ) {
                return source;
            }
        }
        return null;
    }

    /**
     * Get the tar-archive-source of this update. A tar-archive is usually only available for GitLab updates. GitHub typically
     * only provides zip- and tar.gz-archives.
     * @author Griefed
     * @return {@link Source} of {@link ArchiveType#TAR} of this update, wrapped in an {@link Optional}.
     */
    public Optional<Source> sourceTar() {
        for (Source source : SOURCES) {
            if (source.type() == ArchiveType.TAR) {
                return Optional.of(source);
            }
        }
        return Optional.empty();
    }

    /**
     * Get the tar.bz2-archive-source of this update. A tar.bz2-archive is usually only available for GitLab updates. GitHub typically
     * only provides zip- and tar.gz-archives.
     * @author Griefed
     * @return {@link Source} of {@link ArchiveType#TAR_BZ2} of this update, wrapped in an {@link Optional}.
     */
    public Optional<Source> sourceTarBz2() {
        for (Source source : SOURCES) {
            if (source.type() == ArchiveType.TAR_BZ2) {
                return Optional.of(source);
            }
        }
        return Optional.empty();
    }

    /**
     * Get a specific release-asset for a given name of said release.
     * @author Griefed
     * @param releaseName {@link String} The name of the release asset.
     * @return {@link ReleaseAsset} for the given name, wrapped in an {@link Optional}.
     */
    public Optional<ReleaseAsset> getReleaseAsset(@NotNull String releaseName) {
        if (ASSETS == null) {
            return Optional.empty();
        }

        for (ReleaseAsset releaseAsset : ASSETS) {
            if (releaseAsset.name().equals(releaseName)) {
                return Optional.of(releaseAsset);
            }
        }
        return Optional.empty();
    }
}
