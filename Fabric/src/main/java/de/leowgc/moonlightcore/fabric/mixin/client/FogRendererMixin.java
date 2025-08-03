package de.leowgc.moonlightcore.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.shaders.FogShape;
import de.leowgc.moonlightcore.api.event.EventDispatcher;
import de.leowgc.moonlightcore.api.event.client.FogRenderEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Shadow
    private static float fogRed;
    @Shadow
    private static float fogGreen;
    @Shadow
    private static float fogBlue;

    @Inject(method = "setupColor", at = @At("HEAD"))
    private static void mlcore_setupColor(Camera camera, float partialTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo ci) {
        FogRenderEvent.FogColorCompute.Context ctx = new FogRenderEvent.FogColorCompute.Context() {
            @Override
            public void setRed(float red) {
                fogRed = red;
            }

            @Override
            public void setGreen(float green) {
                fogGreen = green;
            }

            @Override
            public void setBlue(float blue) {
                fogBlue = blue;
            }

            @Override
            public float getRed() {
                return fogRed;
            }

            @Override
            public float getGreen() {
                return fogGreen;
            }

            @Override
            public float getBlue() {
                return fogBlue;
            }

            @Override
            public Camera getCamera() {
                return camera;
            }
        };

        EventDispatcher.INSTANCE.fire(FogRenderEvent.FOG_COLOR_COMPUTE, (listener) -> listener.bootstrap(Minecraft.getInstance().gameRenderer, ctx, partialTicks));
    }

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void mlcore_setupFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean bl, float f, CallbackInfo ci, @Local FogType fogType, @Local FogRenderer.FogData data){
        FogRenderEvent.FogRendering.Context context = new FogRenderEvent.FogRendering.Context() {
            private float farPlaneDistance = data.start;
            private float nearPlaneDistance = data.start;
            private FogShape fogShape = data.shape;

            @Override
            public void setFarPlaneDistance(float distance) {
                farPlaneDistance = distance;
            }

            @Override
            public void setNearPlaneDistance(float distance) {
                nearPlaneDistance = distance;
            }

            @Override
            public void setFogShape(FogShape shape) {
                fogShape = shape;
            }

            @Override
            public void scaleFarPlaneDistance(float factor) {
                farPlaneDistance *= factor;
            }

            @Override
            public void scaleNearPlaneDistance(float factor) {
                nearPlaneDistance *= factor;
            }

            @Override
            public float getFarPlaneDistance() {
                return farPlaneDistance;
            }

            @Override
            public float getNearPlaneDistance() {
                return nearPlaneDistance;
            }

            @Override
            public Camera getCamera() {
                return camera;
            }

            @Override
            public FogShape getFogShape() {
                return fogShape;
            }

            @Override
            public FogRenderer.FogMode getMode() {
                return fogMode;
            }

            @Override
            public FogType getType() {
                return fogType;
            }
        };

        EventDispatcher.INSTANCE.fire(FogRenderEvent.FOG_RENDERING, (listener) -> listener.bootstrap(Minecraft.getInstance().gameRenderer, context, f));
    }
}

