package de.leoxian.moonlightcore.fabric.mixin.client;

import de.leoxian.moonlightcore.event.common.ServerLifecycleEvent;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {

    @Inject(method = "initServer", at = @At("RETURN"))
    private void mlcore_initServer(CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValueZ()) {
            ServerLifecycleEvent.STARTING.invoker().onLifecycleState((MinecraftServer) (Object) this);
        }
    }
}
