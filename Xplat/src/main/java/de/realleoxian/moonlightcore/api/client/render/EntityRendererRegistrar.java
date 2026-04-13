package de.realleoxian.moonlightcore.api.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

public interface EntityRendererRegistrar {
    <E extends Entity> void register(Supplier<EntityType<E>> entityType, EntityRendererProvider<E> provider);
}
