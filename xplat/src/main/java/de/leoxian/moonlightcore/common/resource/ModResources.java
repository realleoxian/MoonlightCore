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

    Collection<Path> getRootPaths();

    Optional<ModResource> find(String relativePath);

    boolean hasFile(String relativePath);

    void visitContent(String startFolder, ModResourceVisitor visitor);

    default void visitContent(ModResourceVisitor visitor) {
        visitContent("", visitor);
    }
}
