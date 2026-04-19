package de.realleoxian.moonlightcore.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import de.realleoxian.moonlightcore.api.client.event.ViewportEvents;
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

    @Inject(
            method = "setupColor",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private static void moonlightcore$fireComputeFogColor(Camera activeRenderInfo, float partialTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo ci) {
        var context = new ViewportEvents.ComputeFogColor.Context() {
            float red = fogRed;
            float green = fogGreen;
            float blue = fogBlue;

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
        };

        if (ViewportEvents.COMPUTE_FOG_COLOR.invoker().onComputeFogColor(Minecraft.getInstance().gameRenderer, context, partialTicks).isTrue()) {
            fogRed = context.red;
            fogGreen = context.green;
            fogBlue = context.blue;
            ci.cancel();
        }
    }

    @Inject(
            method = "setupFog",
            at = @At(value = "RETURN")
    )
    private static void moonlightcore$fireRenderFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean bl, float f, CallbackInfo ci, @Local FogType fogType, @Local FogRenderer.FogData fogData) {
        var context = new ViewportEvents.RenderFog.Context() {
            float start = fogData.start;
            float end = fogData.end;
            FogShape shape = fogData.shape;

            @Override
            public void setStartDistance(float distance) {
                this.start = distance;
            }

            @Override
            public void setEndDistance(float distance) {
                this.end = distance;
            }

            @Override
            public void setShape(FogShape shape) {
                this.shape = shape;
            }

            @Override
            public float getStartDistance() {
                return this.start;
            }

            @Override
            public float getEndDistance() {
                return this.end;
            }

            @Override
            public FogShape getShape() {
                return this.shape;
            }

            @Override
            public FogRenderer.FogMode getMode() {
                return fogMode;
            }
        };

        if (ViewportEvents.RENDER_FOG.invoker().onRenderFog(Minecraft.getInstance().gameRenderer, camera, context, f).isTrue()) {
            RenderSystem.setShaderFogStart(context.start);
            RenderSystem.setShaderFogEnd(context.end);
            RenderSystem.setShaderFogShape(context.shape);
        }
    }
}
