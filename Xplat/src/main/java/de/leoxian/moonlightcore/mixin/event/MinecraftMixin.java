package de.leoxian.moonlightcore.mixin.event;

import de.leoxian.moonlightcore.event.client.ClientLifecycleEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

     @Inject(method = "run", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;gameThread:Ljava/lang/Thread;", shift = At.Shift.AFTER, ordinal = 0))
     public void mlcore_onClientStarted(CallbackInfo ci) {
          ClientLifecycleEvent.STARTED.invoker().onLifecycleState((Minecraft) (Object) this);
     }

     @Inject(method = "stop", at = @At(value = "TAIL"))
     public void mlcore_onClientStopped(CallbackInfo ci) {
          ClientLifecycleEvent.STOPPING.invoker().onLifecycleState((Minecraft) (Object) this);
     }

}
