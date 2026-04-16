package de.realleoxian.moonlightcore.forge.client.render;

import com.mojang.datafixers.util.Pair;
import de.realleoxian.moonlightcore.api.client.render.EntityRendererRegistrar;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ForgeEntityRendererRegistrar implements EntityRendererRegistrar {
    private final List<Pair<Supplier<EntityType<?>>, EntityRendererProvider<Entity>>> providers = new ArrayList<>();

    @SubscribeEvent
    public void onRegisterEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
        providers.forEach(p -> event.registerEntityRenderer(p.getFirst().get(), p.getSecond()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Entity> void register(Supplier<EntityType<E>> entityType, EntityRendererProvider<E> provider) {
        this.providers.add(Pair.of(entityType::get, (EntityRendererProvider<Entity>) provider));
    }
}
