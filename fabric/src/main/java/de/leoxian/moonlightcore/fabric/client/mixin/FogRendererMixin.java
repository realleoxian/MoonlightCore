package de.leoxian.moonlightcore.fabric.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import de.leoxian.moonlightcore.client.event.ViewportEvents;
import de.leoxian.moonlightcore.common.event.base.CompoundEventResult;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Unique
    private static final ThreadLocal<FogEnvironment> moonlightcore$fogEnvironment = new ThreadLocal<>();

    @Inject(
            method = "setupFog",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/fog/environment/FogEnvironment;setupFog(Lnet/minecraft/client/renderer/fog/FogData;Lnet/minecraft/client/Camera;Lnet/minecraft/client/multiplayer/ClientLevel;FLnet/minecraft/client/DeltaTracker;)V"
            )
    )
    private void moonlightcore$fetchFogEnvironment(Camera camera, int renderDistanceInChunks, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level, CallbackInfoReturnable<FogData> cir, @Local FogEnvironment fogEnvironment) {
        moonlightcore$fogEnvironment.set(fogEnvironment);
    }

    @Inject(
            method = "setupFog",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void moonlightcore$dispatchRenderFogEvent(Camera camera, int renderDistanceInChunks, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level, CallbackInfoReturnable<FogData> cir) {
        FogData originalData = cir.getReturnValue();
        FogEnvironment fogEnvironment = moonlightcore$fogEnvironment.get();
        moonlightcore$fogEnvironment.remove();

        CompoundEventResult<FogData> result = ViewportEvents.RENDER_FOG.doFire().onRenderFog(Minecraft.getInstance().gameRenderer, camera, deltaTracker.getGameTimeDeltaPartialTick(false), fogEnvironment, originalData);
        if (result.result().isSuccess() && result.isValuePresent() && result.value() != originalData) {
            cir.setReturnValue(result.value());
        }
    }

    @Inject(
            method = "computeFogColor",
            at = @At(
                    value = "RETURN"
            )
    )
    private void moonlightcore$dispatchComputeFogColor(Camera camera, float partialTicks, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f dest, CallbackInfo ci) {
        ViewportEvents.ComputeFogColor.Context context = new ViewportEvents.ComputeFogColor.Context() {
            float red = dest.x;
            float green = dest.y;
            float blue = dest.z;

            @Override
            public void red(float red) {
                this.red = red;
            }

            @Override
            public float red() {
                return this.red;
            }

            @Override
            public void green(float green) {
                this.green = green;
            }

            @Override
            public float green() {
                return this.green;
            }

            @Override
            public void blue(float blue) {
                this.blue = blue;
            }

            @Override
            public float blue() {
                return this.blue;
            }
        };

        if (ViewportEvents.COMPUTE_FOG_COLOR.doFire().onComputeFogColor(Minecraft.getInstance().gameRenderer, camera, partialTicks, context).isSuccess()) {
            dest.set(context.red(), context.green(), context.blue(), 1.0F);
        }
    }
}
