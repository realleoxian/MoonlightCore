package de.leoxian.moonlightcore.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

public interface EntityRendererRegistrar {
    <T extends Entity> void register(Supplier<EntityType<T>> entityType, EntityRendererProvider<T> provider);
}
