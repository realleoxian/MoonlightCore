package de.leoxian.moonlightcore.neoforge.common.resource;

import de.leoxian.moonlightcore.common.resource.ModResource;
import de.leoxian.moonlightcore.common.resource.ModResourceVisitor;
import de.leoxian.moonlightcore.common.resource.ModResources;
import net.neoforged.fml.jarcontents.JarContents;
import net.neoforged.fml.jarcontents.JarResource;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

public record NeoforgeModResources(JarContents contents) implements ModResources {
    @Override
    public Collection<Path> getRootPaths() {
        return contents.getContentRoots();
    }

    @Override
    public Optional<ModResource> find(String relativePath) {
        JarResource resource = contents.get(relativePath);
        if (resource != null) {
            return Optional.of(new NeoforgeModResource(resource, relativePath));
        }
        return Optional.empty();
    }

    @Override
    public boolean hasFile(String relativePath) {
        return contents.containsFile(relativePath);
    }

    @Override
    public void visitContent(String startFolder, ModResourceVisitor visitor) {
        contents().visitContent(startFolder, (relativePath, resource) -> visitor.visit(new NeoforgeModResource(resource, relativePath)));
    }
}
