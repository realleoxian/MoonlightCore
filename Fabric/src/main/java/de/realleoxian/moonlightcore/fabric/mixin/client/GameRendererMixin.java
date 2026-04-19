package de.realleoxian.moonlightcore.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.realleoxian.moonlightcore.api.client.event.ViewportEvents;
import de.realleoxian.moonlightcore.mixin.client.CameraInvoker;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V",
                    shift = At.Shift.AFTER
            )
    )
    private void moonlightcore$fireComputeCameraAngle(float partialTicks, long finishTimeNano, PoseStack poseStack, CallbackInfo ci, @Local Camera camera) {
        var context = new ViewportEvents.ComputeCameraAngle.Context() {
            float yaw = camera.getYRot();
            float pitch = camera.getXRot();
            float roll = 0.0F;

            @Override
            public void setYaw(float yaw) {
                this.yaw = yaw;
            }

            @Override
            public void setPitch(float pitch) {
                this.pitch = pitch;
            }

            @Override
            public void setRoll(float roll) {
                this.roll = roll;
            }

            @Override
            public float getYaw() {
                return this.yaw;
            }

            @Override
            public float getPitch() {
                return this.pitch;
            }

            @Override
            public float getRoll() {
                return this.roll;
            }
        };

        if (ViewportEvents.COMPUTE_CAMERA_ANGLE.invoker().onComputeCameraAngle((GameRenderer) (Object) GameRendererMixin.this, camera, context, partialTicks).isTrue()) {
            ((CameraInvoker) camera).invokeSetRotation(context.yaw, context.pitch);
            poseStack.mulPose(Axis.ZP.rotationDegrees(context.roll));
        }
    }
}
