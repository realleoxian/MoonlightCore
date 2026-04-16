package de.realleoxian.moonlightcore.forge.mixin.client;

import de.realleoxian.moonlightcore.api.client.event.ClientEntityEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientLevel$EntityCallbacks")
public class ClientLevelEntityCallbacksMixin {
    @Shadow
    @Final
    ClientLevel this$0;

    @Inject(
            method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "TAIL")
    )
    private void moonlightcore$fireEntityLoad(Entity p_171712_, CallbackInfo ci) {
        ClientEntityEvents.LOAD.invoker().onLoad(this.this$0, p_171712_);
    }

    @Inject(
            method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "HEAD")
    )
    private void moonlightcore$fireEntityUnload(Entity p_171716_, CallbackInfo ci) {
        ClientEntityEvents.UNLOAD.invoker().onUnload(this.this$0, p_171716_);
    }
}
