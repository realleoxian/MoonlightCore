package de.realleoxian.moonlightcore.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraInvoker {
    @Invoker
    void invokeSetPosition(Vec3 position);

    @Invoker
    void invokeSetRotation(float yRot, float xRot);
}
