package de.leoxian.moonlightcore.common.resource;

@FunctionalInterface
public interface ModResourceVisitor {
    void visit(String relativePath, ModResource resource);
}
