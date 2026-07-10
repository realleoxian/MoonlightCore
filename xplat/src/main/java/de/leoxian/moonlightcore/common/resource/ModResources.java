package de.leoxian.moonlightcore.common.resource;

import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

@ApiStatus.NonExtendable
public interface ModResources {
    Collection<Path> getRootPaths();

    Optional<ModResource> find(String relativePath);

    boolean hasFile(String relativePath);

    void visitContent(String startFolder, ModResourceVisitor visitor);

    default void visitContent(ModResourceVisitor visitor) {
        visitContent("", visitor);
    }
}
