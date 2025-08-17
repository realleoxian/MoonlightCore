package de.leowgc.moonlightcore.forge.handler;

import de.leowgc.moonlightcore.api.event.EventDispatcher;
import de.leowgc.moonlightcore.api.event.client.GameRenderingEvents;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Arrays;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetupEventHandler {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        GameRenderingEvents.BlockRendererRegistration.Output blockRendererOutput = new GameRenderingEvents.BlockRendererRegistration.Output() {
            @Override
            public void register(RenderType renderType, Block... blocks) {
                Arrays.stream(blocks).forEach((block) -> ItemBlockRenderTypes.setRenderLayer(block, renderType));
            }

            @Override
            public void register(RenderType renderType, Fluid... fluids) {
                Arrays.stream(fluids).forEach((block) -> ItemBlockRenderTypes.setRenderLayer(block, renderType));
            }
        };

        EventDispatcher.INSTANCE.fire(GameRenderingEvents.BLOCK_RENDERER_REGISTRATION, (listener) -> listener.bootstrap(blockRendererOutput));
    }

    @SubscribeEvent
    public static void onBlockColorHandlerRegistration(RegisterColorHandlersEvent.Block event) {
        GameRenderingEvents.BlockColorRegistration.Output output = new GameRenderingEvents.BlockColorRegistration.Output() {
            @Override
            public void register(ItemColor color, ItemLike... items) {}

            @Override
            public void register(BlockColor color, Block... blocks) {
                event.register(color, blocks);
            }
        };

        EventDispatcher.INSTANCE.fire(GameRenderingEvents.BLOCK_COLOR_REGISTRATION, (listener) -> listener.bootstrap(output));
    }

    @SubscribeEvent
    public static void onBlockColorHandlerRegistration(RegisterColorHandlersEvent.Item event) {
        GameRenderingEvents.BlockColorRegistration.Output output = new GameRenderingEvents.BlockColorRegistration.Output() {
            @Override
            public void register(ItemColor color, ItemLike... items) {
                event.register(color, items);
            }

            @Override
            public void register(BlockColor color, Block... blocks) {}
        };

        EventDispatcher.INSTANCE.fire(GameRenderingEvents.BLOCK_COLOR_REGISTRATION, (listener) -> listener.bootstrap(output));
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        GameRenderingEvents.EntityRendererRegistration.Output entityRendererOutput = event::registerEntityRenderer;
        GameRenderingEvents.BlockEntityRendererRegistration.Output blockEntityRendererOutput = event::registerBlockEntityRenderer;

        EventDispatcher.INSTANCE.fire(GameRenderingEvents.ENTITY_RENDERER_REGISTRATION, (listener) -> listener.bootstrap(entityRendererOutput));
        EventDispatcher.INSTANCE.fire(GameRenderingEvents.BLOCK_ENTITY_RENDERER_REGISTRATION, (listener) -> listener.bootstrap(blockEntityRendererOutput));
    }

    @SubscribeEvent
    public static void onRegisterModelLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        GameRenderingEvents.ModelLayerRegistration.Output modelLayerOutput = event::registerLayerDefinition;
        EventDispatcher.INSTANCE.fire(GameRenderingEvents.MODEL_LAYER_REGISTRATION, (listener) -> listener.bootstrap(modelLayerOutput));
    }

    @SubscribeEvent
    public static void onRegisterParticleProvider(RegisterParticleProvidersEvent event) {
        GameRenderingEvents.ParticleFactoryRegistration.Output particleFactoryOutput = new GameRenderingEvents.ParticleFactoryRegistration.Output() {
            @Override
            public <T extends ParticleOptions, P extends ParticleType<T>> void register(Supplier<P> type, ParticleProvider<T> provider) {
                event.registerSpecial(type.get(), provider);
            }
        };

        EventDispatcher.INSTANCE.fire(GameRenderingEvents.PARTICLE_FACTORY_REGISTRATION, (listener) -> listener.bootstrap(particleFactoryOutput));
    }

}
