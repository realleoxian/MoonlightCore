package de.leoxian.moonlightcore.common;

import de.leoxian.moonlightcore.common.resource.ModResource;
import de.leoxian.moonlightcore.common.resource.ModResourceVisitor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ApiStatus.NonExtendable
public interface ModContainer {
    ModMetadata metadata();

    void visitResources(Path directory, ModResourceVisitor visitor);

    @Nullable
    ModResource findResource(final Path path);

    default boolean hasResource(final Path path) {
        return findResource(path) != null;
    }
}
