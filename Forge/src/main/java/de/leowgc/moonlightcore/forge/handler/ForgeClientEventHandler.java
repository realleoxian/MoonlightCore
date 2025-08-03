package de.leowgc.moonlightcore.forge.handler;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.vertex.PoseStack;
import de.leowgc.moonlightcore.api.event.EventDispatcher;
import de.leowgc.moonlightcore.api.event.client.ClientTickEvent;
import de.leowgc.moonlightcore.api.event.client.FogRenderEvent;
import de.leowgc.moonlightcore.api.event.client.HudRenderEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        switch (event.phase) {
            case START -> EventDispatcher.INSTANCE.fire(ClientTickEvent.CLIENT_TICK, (listener) -> listener.bootstrap(ClientTickEvent.Phase.START));
            case END -> EventDispatcher.INSTANCE.fire(ClientTickEvent.CLIENT_TICK, (listener) -> listener.bootstrap(ClientTickEvent.Phase.END));
        }
    }

}
