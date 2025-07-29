package de.leoxian.moonlightcore.fabric;

import de.leoxian.moonlightcore.api.event.EventDispatcher;
import de.leoxian.moonlightcore.api.event.client.ClientTickEvent;
import de.leoxian.moonlightcore.api.event.client.GameRenderingEvents;
import de.leoxian.moonlightcore.api.event.client.HudRenderEvent;
import de.leoxian.moonlightcore.fabric.api.MoonlightCoreInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public class MoonlightCoreFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance().getEntrypointContainers("moonlightcore", MoonlightCoreInitializer.class).stream().map(EntrypointContainer::getEntrypoint).forEach(MoonlightCoreInitializer::onClientInitialized);

        this.setupRenderingEvents();

        HudRenderCallback.EVENT.register((guiGraphics, partialTicks) -> {
            HudRenderEvent.Context ctx = new HudRenderEvent.Context() {};
            EventDispatcher.INSTANCE.fire(HudRenderEvent.HUD_RENDER, (listener) -> listener.bootstrap(ctx, guiGraphics.pose(), partialTicks));
        });

        ClientTickEvents.START_CLIENT_TICK.register(($) -> EventDispatcher.INSTANCE.fire(ClientTickEvent.CLIENT_TICK, (listener) -> listener.bootstrap(ClientTickEvent.Phase.START)));
        ClientTickEvents.END_CLIENT_TICK.register(($) -> EventDispatcher.INSTANCE.fire(ClientTickEvent.CLIENT_TICK, (listener) -> listener.bootstrap(ClientTickEvent.Phase.END)));
    }

    private void setupRenderingEvents() {
        EventDispatcher.INSTANCE.fire(GameRenderingEvents.BLOCK_COLOR_REGISTRATION, (listener) -> listener.bootstrap(new GameRenderingEvents.BlockColorRegistration.Output() {
            @Override
            public void register(ItemColor color, ItemLike... items) {
                ColorProviderRegistry.ITEM.register(color, items);
            }

            @Override
            public void register(BlockColor color, Block... blocks) {
                ColorProviderRegistry.BLOCK.register(color, blocks);
            }
        }));

        EventDispatcher.INSTANCE.fire(GameRenderingEvents.BLOCK_RENDERER_REGISTRATION, (listener) -> listener.bootstrap(new GameRenderingEvents.BlockRendererRegistration.Output() {
            @Override
            public void register(RenderType renderType, Block... blocks) {
                BlockRenderLayerMap.INSTANCE.putBlocks(renderType, blocks);
            }

            @Override
            public void register(RenderType renderType, Fluid... fluids) {
                BlockRenderLayerMap.INSTANCE.putFluids(renderType, fluids);
            }
        }));

        EventDispatcher.INSTANCE.fire(GameRenderingEvents.ENTITY_RENDERER_REGISTRATION, (listener -> listener.bootstrap(EntityRendererRegistry::register)));

        EventDispatcher.INSTANCE.fire(GameRenderingEvents.BLOCK_ENTITY_RENDERER_REGISTRATION, (listener) -> listener.bootstrap(BlockEntityRenderers::register));

        EventDispatcher.INSTANCE.fire(GameRenderingEvents.MODEL_LAYER_REGISTRATION, (listener) -> listener.bootstrap((layer, definition) -> EntityModelLayerRegistry.registerModelLayer(layer, definition::get)));

        EventDispatcher.INSTANCE.fire(GameRenderingEvents.PARTICLE_FACTORY_REGISTRATION, (listener) -> listener.bootstrap(new GameRenderingEvents.ParticleFactoryRegistration.Output() {
            @Override
            public <T extends ParticleOptions, P extends ParticleType<T>> void register(Supplier<P> type, ParticleProvider<T> provider) {
                ParticleFactoryRegistry.getInstance().register(type.get(), provider);
            }
        }));
    }

}
