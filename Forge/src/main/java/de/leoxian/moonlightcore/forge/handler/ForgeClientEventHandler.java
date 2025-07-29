package de.leoxian.moonlightcore.forge.handler;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.vertex.PoseStack;
import de.leoxian.moonlightcore.api.event.EventDispatcher;
import de.leoxian.moonlightcore.api.event.client.ClientTickEvent;
import de.leoxian.moonlightcore.api.event.client.FogRenderEvent;
import de.leoxian.moonlightcore.api.event.client.GameRenderingEvents;
import de.leoxian.moonlightcore.api.event.client.HudRenderEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Arrays;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public final class ForgeClientEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderHudEvent(RenderGuiOverlayEvent.Post event) {
        HudRenderEvent.Context context = new HudRenderEvent.Context() {};
        PoseStack poseStack = event.getGuiGraphics().pose();
        float partialTick = event.getPartialTick();

        EventDispatcher.INSTANCE.fire(HudRenderEvent.HUD_RENDER, (listener) -> listener.bootstrap(context, poseStack, partialTick));
    }

    @SubscribeEvent
    public static void onRenderFogColor(ViewportEvent.ComputeFogColor event) {
        FogRenderEvent.FogColorCompute.Context context = new FogRenderEvent.FogColorCompute.Context() {
            @Override
            public void setRed(float red) {
                event.setRed(red);
            }

            @Override
            public void setGreen(float green) {
                event.setGreen(green);
            }

            @Override
            public void setBlue(float blue) {
                event.setBlue(blue);
            }

            @Override
            public float getRed() {
                return event.getRed();
            }

            @Override
            public float getGreen() {
                return event.getGreen();
            }

            @Override
            public float getBlue() {
                return event.getBlue();
            }

            @Override
            public Camera getCamera() {
                return event.getCamera();
            }
        };

        EventDispatcher.INSTANCE.fire(FogRenderEvent.FOG_COLOR_COMPUTE, (listener) -> listener.bootstrap(event.getRenderer(), context, (float) event.getPartialTick()));
    }

    @SubscribeEvent
    public static void onFogRendering(ViewportEvent.RenderFog event) {
        FogRenderEvent.FogRendering.Context context = new FogRenderEvent.FogRendering.Context() {
            @Override
            public void setFarPlaneDistance(float distance) {
                event.setFarPlaneDistance(distance);
            }

            @Override
            public void setNearPlaneDistance(float distance) {
                event.setNearPlaneDistance(distance);
            }

            @Override
            public void setFogShape(FogShape shape) {
                event.setFogShape(shape);
            }

            @Override
            public void scaleFarPlaneDistance(float factor) {
                event.scaleFarPlaneDistance(factor);
            }

            @Override
            public void scaleNearPlaneDistance(float factor) {
                event.scaleNearPlaneDistance(factor);
            }

            @Override
            public float getFarPlaneDistance() {
                return event.getFarPlaneDistance();
            }

            @Override
            public float getNearPlaneDistance() {
                return event.getNearPlaneDistance();
            }

            @Override
            public Camera getCamera() {
                return event.getCamera();
            }

            @Override
            public FogShape getFogShape() {
                return event.getFogShape();
            }

            @Override
            public FogRenderer.FogMode getMode() {
                return event.getMode();
            }

            @Override
            public FogType getType() {
                return event.getType();
            }
        };

        EventDispatcher.INSTANCE.fire(FogRenderEvent.FOG_RENDERING, (listener) -> listener.bootstrap(event.getRenderer(), context, (float) event.getPartialTick()));
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

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        switch (event.phase) {
            case START -> EventDispatcher.INSTANCE.fire(ClientTickEvent.CLIENT_TICK, (listener) -> listener.bootstrap(ClientTickEvent.Phase.START));
            case END -> EventDispatcher.INSTANCE.fire(ClientTickEvent.CLIENT_TICK, (listener) -> listener.bootstrap(ClientTickEvent.Phase.END));
        }
    }

    private ForgeClientEventHandler() {}

}
