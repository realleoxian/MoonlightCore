package de.leoxian.moonlightcore.neoforge.client.render;

import de.leoxian.moonlightcore.client.render.EntityRendererRegistrar;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.function.Supplier;

public record NeoforgeEntityRendererRegistrar(EntityRenderersEvent.RegisterRenderers event) implements EntityRendererRegistrar {
    @Override
    public <T extends Entity> void register(Supplier<EntityType<T>> entityType, EntityRendererProvider<T> provider) {
        event.registerEntityRenderer(entityType.get(), provider);
    }
}
