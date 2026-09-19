package de.leoxian.moonlightcore.fabric.client.render;

import de.leoxian.moonlightcore.client.render.EntityRendererRegistrar;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

public enum FabricEntityRendererRegistrar implements EntityRendererRegistrar {
    INSTANCE
    ;

    @Override
    public <T extends Entity> void register(Supplier<EntityType<T>> entityType, EntityRendererProvider<T> provider) {
        EntityRenderers.register(entityType.get(), provider);
    }
}
