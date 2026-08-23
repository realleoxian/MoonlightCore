package de.leoxian.moonlightcore.fabric.client.mixin;

import de.leoxian.moonlightcore.client.event.ViewportEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {
    @Shadow
    private float xRot;
    @Shadow
    private float yRot;
    @Shadow @Final
    private Quaternionf rotation;
    @Shadow @Final
    private Vector3f forwards;
    @Shadow @Final
    private Vector3f up;
    @Shadow @Final
    private Vector3f left;
    @Shadow @Final
    private static Vector3f FORWARDS;
    @Shadow @Final
    private static Vector3f UP;
    @Shadow @Final
    private static Vector3f LEFT;

    @Inject(
            method = "alignWithEntity",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$dispatchComputeCameraAnglesEvent(float partialTicks, CallbackInfo ci) {
        ViewportEvents.ComputeCameraAngles.Context context = new ViewportEvents.ComputeCameraAngles.Context() {
            private float yaw = CameraMixin.this.yRot;
            private float pitch = CameraMixin.this.xRot;
            private float roll = 0.0F;

            @Override public void yaw(float yaw) {
                this.yaw = yaw;
            }
            @Override public float yaw() {
                return this.yaw;
            }

            @Override public void pitch(float pitch) {
                this.pitch = pitch;
            }
            @Override public float pitch() {
                return this.pitch;
            }

            @Override public void roll(float roll) {
                this.roll = roll;
            }
            @Override public float roll() {
                return this.roll;
            }
        };

        if (ViewportEvents.COMPUTE_CAMERA_ANGLES.doFire().onComputeCameraAngles(Minecraft.getInstance().gameRenderer, (Camera) (Object) this, partialTicks, context).isSuccess()) {
            this.xRot = context.pitch();
            this.yRot = context.yaw();

            this.rotation.rotationYXZ(
                    (float) Math.PI - this.yRot * (float) (Math.PI / 180.0),
                    -this.xRot * (float) (Math.PI / 180.0),
                    -context.roll() * (float) (Math.PI / 180.0)
            );

            FORWARDS.rotate(this.rotation, this.forwards);
            UP.rotate(this.rotation, this.up);
            LEFT.rotate(this.rotation, this.left);
        }
    }
}
