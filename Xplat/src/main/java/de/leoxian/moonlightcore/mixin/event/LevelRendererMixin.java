package de.leoxian.moonlightcore.mixin.event;

import com.mojang.blaze3d.vertex.PoseStack;
import de.leoxian.moonlightcore.event.client.LevelRenderEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
     @Shadow
     private int ticks;

     @Unique
     private Frustum mlcore_frustum = null;

     @Inject(method = "setupRender", at = @At("TAIL"))
     public void mlcore_setupRender(Camera camera, Frustum frustum, boolean hasCapturedFrustum, boolean isSpectator, CallbackInfo ci) {
          this.mlcore_frustum = frustum;
     }

     @Inject(method = "renderChunkLayer", at = @At("TAIL"))
     public void mlcore_renderChunkLayer(RenderType renderType, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, CallbackInfo ci) {
          LevelRenderEvent.Stage stage = LevelRenderEvent.Stage.byRenderType(renderType);

          if(stage != null) {
               this.mlcore_dispatchStage(stage, poseStack, projectionMatrix);
          }
     }

     @Inject(method = "renderLevel", at = @At(
             value = "INVOKE",
             target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V",
             shift = At.Shift.AFTER))
     public void mlcore_afterSkyStage(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
          this.mlcore_dispatchStage(LevelRenderEvent.Stage.AFTER_SKY, poseStack, projectionMatrix);
     }

     @Inject(method = "renderLevel", at = @At(
             value = "INVOKE_STRING",
             target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
             args = "ldc=blockentities"
     ))
     public void mlcore_afterEntities(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
          this.mlcore_dispatchStage(LevelRenderEvent.Stage.AFTER_ENTITIES, poseStack, projectionMatrix);
     }

     @Inject(method = "renderLevel", at = @At(
             value = "INVOKE_STRING",
             target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
             args = "ldc=destroyProgress"
     ))
     public void mlcore_afterBlockEntities(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
          this.mlcore_dispatchStage(LevelRenderEvent.Stage.AFTER_BLOCK_ENTITIES, poseStack, projectionMatrix);
     }

     @Inject(method = "renderLevel", at = @At(
             value = "INVOKE_STRING",
             target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
             args = "ldc=particles"))
     public void mlcore_afterParticles(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
          this.mlcore_dispatchStage(LevelRenderEvent.Stage.AFTER_PARTICLES, poseStack, projectionMatrix);
     }

     @Inject(method = "renderLevel", at = @At(
             value = "INVOKE",
             target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V",
             shift = At.Shift.AFTER
     ))
     public void mlcore_afterWeather(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
          this.mlcore_dispatchStage(LevelRenderEvent.Stage.AFTER_WEATHER, poseStack, projectionMatrix);
     }

     @Inject(method = "renderLevel", at = @At("TAIL"))
     public void mlcore_afterLevel(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
          this.mlcore_dispatchStage(LevelRenderEvent.Stage.AFTER_LEVEL, poseStack, projectionMatrix);
     }


     @Unique
     private void mlcore_dispatchStage(LevelRenderEvent.Stage stage, PoseStack poseStack, Matrix4f projectionMatrix) {
          Minecraft mc = Minecraft.getInstance();
          ProfilerFiller profiler = mc.getProfiler();

          profiler.push(stage.name);
          LevelRenderEvent.EVENT.invoker().onLevelRendering(stage, (LevelRenderer) (Object) this, mc.gameRenderer.getMainCamera(), this.mlcore_frustum, poseStack, projectionMatrix, this.ticks,  mc.getDeltaFrameTime());
          profiler.pop();
     }
}
