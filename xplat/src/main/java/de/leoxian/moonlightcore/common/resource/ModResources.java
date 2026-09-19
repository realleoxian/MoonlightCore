package de.leoxian.moonlightcore.common.resource;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

@ApiStatus.NonExtendable
public interface ModResources {
    static @Nullable ModResources get(String modId) {
        return XplatAbstraction.INSTANCE.getModResources(modId);
    }

    /// @return All the resources at root level in a mod's jar
    Collection<Path> getRootPaths();

    /// Attempt to retrieve a mod resource from its jar
    /// @param relativePath The relative path where the resource its at
    Optional<ModResource> find(String relativePath);

    /// If a mod's jar has a resource
    /// @param relativePath The path the resource its at
    /// @return Whether the mod's jar has a resource file
    boolean hasFile(String relativePath);

    /// Visits all the content from a starting point directory.
    /// @param startFolder The directory where to start at
    /// @param visitor The resource visitor
    void visitContent(String startFolder, ModResourceVisitor visitor);

    /// Visits all resources from a mod's jar
    /// @param visitor The resource visitor
    default void visitContent(ModResourceVisitor visitor) {
        visitContent("", visitor);
    }
}
