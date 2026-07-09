package de.leoxian.moonlightcore.common;

import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@ApiStatus.NonExtendable
public interface ModContainer {
    ModMetadata metadata();

    List<Path> rootPaths();

    Optional<Path> findPath(String path);
}
