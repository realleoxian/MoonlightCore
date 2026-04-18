package de.realleoxian.moonlightcore.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraInvoker {
    @Invoker("setPosition")
    void setPosition(Vec3 position);

    @Invoker("setRotation")
    void setRotation(float yRot, float xRot);
}
