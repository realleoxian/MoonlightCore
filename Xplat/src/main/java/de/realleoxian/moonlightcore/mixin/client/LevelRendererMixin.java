package de.realleoxian.moonlightcore.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.realleoxian.moonlightcore.api.client.event.LevelRenderEvents;
import de.realleoxian.moonlightcore.api.event.EventBus;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    private int ticks;
    @Shadow
    @Final
    private Minecraft minecraft;
    @Unique private Frustum moonlightcore$frustum = null;

    @Inject(
            method = "setupRender",
            at = @At(value = "TAIL")
    )
    private void moonlightcore$setupRender(Camera camera, Frustum frustum, boolean hasCapturedFrustum, boolean isSpectator, CallbackInfo ci) {
        this.moonlightcore$frustum = frustum;
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void moonlightcore$fireAfterSky(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        moonlightcore$fire(LevelRenderEvents.AFTER_SKY, camera, poseStack, projectionMatrix, partialTick);
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE_STRING",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    args = "ldc=blockentities"
            )
    )
    private void moonlightcore$fireAfterEntities(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        moonlightcore$fire(LevelRenderEvents.AFTER_ENTITIES, camera, poseStack, projectionMatrix, partialTick);
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE_STRING",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    args = "ldc=destroyProgress"
            )
    )
    private void moonlightcore$fireAfterBlockEntities(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        moonlightcore$fire(LevelRenderEvents.AFTER_BLOCK_ENTITIES, camera, poseStack, projectionMatrix, partialTick);
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE_STRING",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    args = "ldc=particles"
            )
    )
    private void moonlightcore$afterParticles(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        moonlightcore$fire(LevelRenderEvents.AFTER_PARTICLES, camera, poseStack, projectionMatrix, partialTick);
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderChunkLayer(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDDLorg/joml/Matrix4f;)V",
                    shift = At.Shift.AFTER
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/RenderType;translucent()Lnet/minecraft/client/renderer/RenderType;"
                    )
            )
    )
    private void moonlightcore$fierAfterTranslucentBlocks(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        moonlightcore$fire(LevelRenderEvents.AFTER_TRANSLUCENT_BLOCKS, camera, poseStack, projectionMatrix, partialTick);
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V",
                    shift = At.Shift.AFTER
            )
    )
    private void moonlightcore$fireAfterWeather(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        moonlightcore$fire(LevelRenderEvents.AFTER_WEATHER, camera, poseStack, projectionMatrix, partialTick);
    }

    @Inject(
            method = "renderLevel",
            at = @At(value = "TAIL")
    )
    private void moonlightcore$fireAfterLevel(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        moonlightcore$fire(LevelRenderEvents.AFTER_LEVEL, camera, poseStack, projectionMatrix, partialTick);
    }

    @Unique private void moonlightcore$fire(EventBus<LevelRenderEvents> event, Camera camera, PoseStack poseStack, Matrix4f projectionMatrix, float partialTick) {
        event.invoker().onLevelRender((LevelRenderer) (Object) this, camera, this.moonlightcore$frustum, poseStack, projectionMatrix, this.ticks, partialTick);
    }
}
