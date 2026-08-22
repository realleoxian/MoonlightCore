package de.leoxian.moonlightcore.client.render;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface EntityRendererRegistrar {
    static void init(String namespace, Consumer<EntityRendererRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.entityRenderers(namespace, initializer);
    }

    <T extends Entity> void register(Supplier<EntityType<T>> entityType, EntityRendererProvider<T> provider);
}
