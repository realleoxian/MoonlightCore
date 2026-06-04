package de.realleoxian.moonlightcore.api.capability;

import net.minecraft.resources.ResourceLocation;

public record CapabilityType<S, T, C>(ResourceLocation id, Class<S> scopeClass, Class<T> capabilityType, Class<C> contextType) {
}
