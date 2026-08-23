package de.leoxian.moonlightcore.fabric.common.resource;

import de.leoxian.moonlightcore.common.resource.ModResource;
import de.leoxian.moonlightcore.common.resource.ModResourceVisitor;
import de.leoxian.moonlightcore.common.resource.ModResources;
import de.leoxian.moonlightcore.internal.common.resource.PathModResource;
import net.fabricmc.loader.api.ModContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public record FabricModResources(ModContainer container) implements ModResources {
    @Override
    public Collection<Path> getRootPaths() {
        return container.getRootPaths();
    }

    @Override
    public Optional<ModResource> find(String relativePath) {
        return container.findPath(relativePath).map(PathModResource::new);
    }

    @Override
    public boolean hasFile(String relativePath) {
        return container.findPath(relativePath).isPresent();
    }

    @Override
    public void visitContent(String startFolder, ModResourceVisitor visitor) {
        container.findPath(startFolder).ifPresent(rootPath -> {
            try (final Stream<Path> walker = Files.walk(rootPath)) {
                walker.forEach(child -> visitor.visit(new PathModResource(child)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
