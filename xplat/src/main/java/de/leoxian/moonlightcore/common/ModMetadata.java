package de.leoxian.moonlightcore.common;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface ModMetadata {
    String id();

    String displayName();

    String version();

    @Nullable
    String issuesUrl();

    @Nullable
    String sourcesUrl();

    @Nullable
    String homepageUrl();
}
