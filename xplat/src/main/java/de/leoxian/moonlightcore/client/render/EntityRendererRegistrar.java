package de.leoxian.moonlightcore.client.render;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface EntityRendererRegistrar {
    /// Configure and register entity type renderers
    /// @param namespace The mod's id
    /// @param initializer The initializer
    static void configure(String namespace, Consumer<EntityRendererRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.entityRenderers(namespace, initializer);
    }

    /// Register the entity renderer provider to the given entity type
    /// @param entityType The entity type
    /// @param provider The renderer provider
    <T extends Entity> void register(Supplier<EntityType<T>> entityType, EntityRendererProvider<T> provider);
}
