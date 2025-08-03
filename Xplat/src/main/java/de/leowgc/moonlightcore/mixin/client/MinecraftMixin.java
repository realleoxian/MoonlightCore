package de.leowgc.moonlightcore.mixin.client;

import de.leowgc.moonlightcore.api.event.EventDispatcher;
import de.leowgc.moonlightcore.api.event.client.ClientLifecycleEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "run", at = @At("HEAD"))
    public void mlcore_onStartingClient(CallbackInfo ci) {
        EventDispatcher.INSTANCE.fire(ClientLifecycleEvent.STARTING, (listener) -> listener.bootstrap((Minecraft) (Object) this));
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;", shift = At.Shift.AFTER))
    public void mlcore_onStartedClient(CallbackInfo ci) {
        EventDispatcher.INSTANCE.fire(ClientLifecycleEvent.STARTED, (listener) -> listener.bootstrap((Minecraft) (Object) this));
    }

    @Inject(method = "close", at = @At("TAIL"))
    public void mlcore_onClientStopping(CallbackInfo ci) {
        EventDispatcher.INSTANCE.fire(ClientLifecycleEvent.STOPPING, (listener) -> listener.bootstrap((Minecraft) (Object) this));
    }

}
