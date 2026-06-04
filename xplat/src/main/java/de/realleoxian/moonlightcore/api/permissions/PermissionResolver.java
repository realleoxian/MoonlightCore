package de.realleoxian.moonlightcore.api.permissions;

@FunctionalInterface
public interface PermissionResolver<T> {
    T resolve(PermissionContext owner, PermissionNode<T> node);
}
