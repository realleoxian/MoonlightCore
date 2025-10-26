package de.leoxian.moonlightcore.fabric.mixin;

import de.leoxian.moonlightcore.event.common.ServerLifecycleEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {

    @Inject(method = "initServer", at = @At("RETURN"), cancellable = true)
    private void mlcore_onDedicatedServerStarting(CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValue()) {
            ServerLifecycleEvent.STARTING.invoker().onLifecycleState((MinecraftServer) (Object) this);
        }
    }
}
