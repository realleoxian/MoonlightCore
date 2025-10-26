package de.leoxian.moonlightcore.fabric;

import de.leoxian.moonlightcore.event.client.ClientLifecycleEvent;
import de.leoxian.moonlightcore.event.client.ClientTickEvent;
import de.leoxian.moonlightcore.event.client.KeyMappingRegistrationEvent;
import de.leoxian.moonlightcore.event.client.RenderingEvents;
import de.leoxian.moonlightcore.event.common.TickEvent;
import de.leoxian.moonlightcore.fabric.api.MoonlightCoreInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

public class MoonlightCoreClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance().getEntrypointContainers("moonlightcore", MoonlightCoreInitializer.class)
                .stream().map(EntrypointContainer::getEntrypoint).forEach(MoonlightCoreInitializer::onClientInitialize);

        ClientLifecycleEvent.SETUP.invoker().run();

        this.setupRenderingEvents();

        KeyMappingRegistrationEvent.EVENT.invoker().onKeyMappingRegistration(KeyBindingHelper::registerKeyBinding);

        ClientTickEvents.START_CLIENT_TICK.register((mc) -> ClientTickEvent.CLIENT_TICK.invoker().onClientTick(TickEvent.Phase.START));
        ClientTickEvents.END_CLIENT_TICK.register((mc) -> ClientTickEvent.CLIENT_TICK.invoker().onClientTick(TickEvent.Phase.END));
    }

    private void setupRenderingEvents() {
        RenderingEvents.BLOCK_COLOR_REGISTRATION.invoker().onBlockColorRegistration(new RenderingEvents.BlockColorRegistration.Output() {
            @Override
            public void register(ItemColor color, ItemLike... items) {
                ColorProviderRegistry.ITEM.register(color, items);
            }

            @Override
            public void register(BlockColor color, Block... blocks) {
                ColorProviderRegistry.BLOCK.register(color, blocks);
            }
        });

        RenderingEvents.BLOCK_RENDER_TYPE_REGISTRATION.invoker().onBlockRendererRegistration(new RenderingEvents.BlockRenderTypeRegistration.Output() {
            @Override
            public void register(RenderType renderType, Block... blocks) {
                BlockRenderLayerMap.INSTANCE.putBlocks(renderType, blocks);
            }

            @Override
            public void register(RenderType renderType, Fluid... fluids) {
                BlockRenderLayerMap.INSTANCE.putFluids(renderType, fluids);
            }
        });

        RenderingEvents.PARTICLE_PROVIDER_REGISTRATION.invoker().onParticleProvidersRegistration(new RenderingEvents.ParticleProviderRegistration.Output() {
            @Override
            public <T extends ParticleOptions> void registerSpriteSet(ParticleType<T> type, ParticleEngine.SpriteParticleRegistration<T> registration) {
                Minecraft.getInstance().particleEngine.register(type, registration);
            }

            @Override
            public <T extends ParticleOptions> void registerSprite(ParticleType<T> type, ParticleProvider.Sprite<T> sprite) {
                Minecraft.getInstance().particleEngine.register(type, sprite);
            }

            @Override
            public <T extends ParticleOptions> void registerSpecial(ParticleType<T> type, ParticleProvider<T> provider) {
                Minecraft.getInstance().particleEngine.register(type, provider);
            }
        });

        RenderingEvents.MODEL_LAYER_REGISTRATION.invoker().onModelLayerRegistration((layerLocation, layerDefinitionSupplier) -> EntityModelLayerRegistry.registerModelLayer(layerLocation, layerDefinitionSupplier::get));

        RenderingEvents.RENDERER_REGISTRATION.invoker().onRendererRegistration(new RenderingEvents.RendererRegistration.Output() {
            @Override
            public <T extends Entity> void registerEntity(EntityType<T> entityType, EntityRendererProvider<T> renderer) {
                EntityRendererRegistry.register(entityType, renderer);
            }

            @Override
            public <T extends BlockEntity> void registerBlockEntity(BlockEntityType<T> blockEntityType, BlockEntityRendererProvider<T> renderer) {
                BlockEntityRenderers.register(blockEntityType, renderer);
            }
        });
    }

}
