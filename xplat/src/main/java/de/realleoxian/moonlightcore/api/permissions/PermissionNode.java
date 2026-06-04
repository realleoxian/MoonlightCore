package de.realleoxian.moonlightcore.api.permissions;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PermissionNode<T>(ResourceLocation name, Class<T> typeValue) {
    @Override
    public @NotNull String toString() {
        return name.toString();
    }
}
