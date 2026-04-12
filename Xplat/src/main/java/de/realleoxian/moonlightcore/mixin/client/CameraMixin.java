package de.realleoxian.moonlightcore.mixin.client;

import de.realleoxian.moonlightcore.impl.client.camerashake.CameraShakeHandlerImpl;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {

    @Inject(
            method = "setup",
            at = @At(
                    value = "RETURN"
            )
    )
    private void moonlightcore$setup(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        CameraShakeHandlerImpl.cameraTick((Camera) (Object) this);
    }

}
