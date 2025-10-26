package de.leoxian.moonlightcore.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import de.leoxian.moonlightcore.event.client.ViewportEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
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

    @Inject(method = "setupColor", at = @At("HEAD"), cancellable = true)
    private static void mlcore_onColorCompute(Camera activeRenderInfo, float partialTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo ci) {
        ViewportEvent.FogColorCompute.Context context = new ViewportEvent.FogColorCompute.Context() {
            float red = fogRed;
            float green = fogGreen;
            float blue = fogBlue;

            @Override
            public float getRed() {
                return red;
            }

            @Override
            public float getGreen() {
                return green;
            }

            @Override
            public float getBlue() {
                return blue;
            }

            @Override
            public void setRed(float red) {
                this.red = red;
            }

            @Override
            public void setGreen(float green) {
                this.green = green;
            }

            @Override
            public void setBlue(float blue) {
                this.blue = blue;
            }
        };

        if(ViewportEvent.FOG_COLOR_COMPUTE.invoker().onColorCompute(Minecraft.getInstance().gameRenderer, context, partialTicks).isTrue()) {
            fogRed = context.getRed();
            fogGreen = context.getGreen();
            fogBlue = context.getBlue();

            ci.cancel();
        }
    }

    @Inject(method = "setupFog", at = @At("TAIL"), cancellable = true)
    private static void mlcore_onFogRendering(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean bl, float f, CallbackInfo ci, @Local FogType fogType, @Local FogRenderer.FogData data) {
        ViewportEvent.RenderFog.Context context = new ViewportEvent.RenderFog.Context() {
            float nearPane = data.start;
            float farPlane = data.end;
            FogShape shape = data.shape;

            @Override
            public void setFarPlaneDistance(float distance) {
                this.farPlane = distance;
            }

            @Override
            public void setNearPlaneDistance(float distance) {
                this.nearPane = distance;
            }

            @Override
            public void setFogShape(FogShape shape) {
                this.shape = shape;
            }

            @Override
            public float getFarPlaneDistance() {
                return this.farPlane;
            }

            @Override
            public float getNearPlaneDistance() {
                return this.nearPane;
            }

            @Override
            public Camera getCamera() {
                return camera;
            }

            @Override
            public FogShape getFogShape() {
                return this.shape;
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

        if(ViewportEvent.RENDER_FOG.invoker().onFogRendering(Minecraft.getInstance().gameRenderer, context, f).isTrue()) {
            RenderSystem.setShaderFogStart(context.getNearPlaneDistance());
            RenderSystem.setShaderFogEnd(context.getFarPlaneDistance());
            RenderSystem.setShaderFogShape(context.getFogShape());

            ci.cancel();
        }
    }
}
