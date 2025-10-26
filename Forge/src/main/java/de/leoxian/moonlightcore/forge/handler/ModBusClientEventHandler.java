package de.leoxian.moonlightcore.forge.handler;

import de.leoxian.moonlightcore.event.client.ClientLifecycleEvent;
import de.leoxian.moonlightcore.event.client.KeyMappingRegistrationEvent;
import de.leoxian.moonlightcore.event.client.RenderingEvents;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Arrays;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModBusClientEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onClientSetup(FMLClientSetupEvent event) {
        ClientLifecycleEvent.SETUP.invoker().run();

        RenderingEvents.BLOCK_RENDER_TYPE_REGISTRATION.invoker().onBlockRendererRegistration(new RenderingEvents.BlockRenderTypeRegistration.Output() {
            @Override
            public void register(RenderType renderType, Block... blocks) {
                Arrays.stream(blocks).forEach((block) -> ItemBlockRenderTypes.setRenderLayer(block, renderType));
            }

            @Override
            public void register(RenderType renderType, Fluid... fluids) {
                Arrays.stream(fluids).forEach((fluid) -> ItemBlockRenderTypes.setRenderLayer(fluid, renderType));
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onKeyMappingRegistration(RegisterKeyMappingsEvent event) {
        KeyMappingRegistrationEvent.EVENT.invoker().onKeyMappingRegistration(event::register);
    }

    @SubscribeEvent
    public static void onBlockColorHandlerRegistration(RegisterColorHandlersEvent.Block event) {
        RenderingEvents.BLOCK_COLOR_REGISTRATION.invoker().onBlockColorRegistration(new RenderingEvents.BlockColorRegistration.Output() {
            @Override
            public void register(ItemColor color, ItemLike... items) {

            }

            @Override
            public void register(BlockColor color, Block... blocks) {
                event.register(color, blocks);
            }
        });
    }

    @SubscribeEvent
    public static void onItemColorHandlerRegistration(RegisterColorHandlersEvent.Item event) {
        RenderingEvents.BLOCK_COLOR_REGISTRATION.invoker().onBlockColorRegistration(new RenderingEvents.BlockColorRegistration.Output() {
            @Override
            public void register(ItemColor color, ItemLike... items) {
                event.register(color, items);
            }

            @Override
            public void register(BlockColor color, Block... blocks) {

            }
        });
    }

    @SubscribeEvent
    public static void onRendererRegistration(EntityRenderersEvent.RegisterRenderers event) {
        RenderingEvents.RENDERER_REGISTRATION.invoker().onRendererRegistration(new RenderingEvents.RendererRegistration.Output() {
            @Override
            public <T extends Entity> void registerEntity(EntityType<T> entityType, EntityRendererProvider<T> renderer) {
                event.registerEntityRenderer(entityType, renderer);
            }

            @Override
            public <T extends BlockEntity> void registerBlockEntity(BlockEntityType<T> blockEntityType, BlockEntityRendererProvider<T> renderer) {
                event.registerBlockEntityRenderer(blockEntityType, renderer);
            }
        });
    }

    @SubscribeEvent
    public static void onParticleProviderRegistration(RegisterParticleProvidersEvent event) {
        RenderingEvents.PARTICLE_PROVIDER_REGISTRATION.invoker().onParticleProvidersRegistration(new RenderingEvents.ParticleProviderRegistration.Output() {
            @Override
            public <T extends ParticleOptions> void registerSpriteSet(ParticleType<T> type, ParticleEngine.SpriteParticleRegistration<T> registration) {
                event.registerSpriteSet(type, registration);
            }

            @Override
            public <T extends ParticleOptions> void registerSprite(ParticleType<T> type, ParticleProvider.Sprite<T> sprite) {
                event.registerSprite(type, sprite);
            }

            @Override
            public <T extends ParticleOptions> void registerSpecial(ParticleType<T> type, ParticleProvider<T> provider) {
                event.registerSpecial(type, provider);
            }
        });
    }

}
