package de.realleoxian.moonlightcore.forge.mixin;

import de.realleoxian.moonlightcore.api.event.ServerEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.level.ServerLevel$EntityCallbacks")
public class ServerLevelEntityCallbacksMixin {
    @Shadow
    @Final
    ServerLevel this$0;

    @Inject(
            method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "TAIL")
    )
    private void moonlightcore$fireEntityLoad(Entity p_143371_, CallbackInfo ci) {
        ServerEntityEvents.LOAD.invoker().onLoad(this.this$0, p_143371_);
    }

    @Inject(
            method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "HEAD")
    )
    private void moonlightcore$fireEntityUnload(Entity p_143375_, CallbackInfo ci) {
        ServerEntityEvents.UNLOAD.invoker().onUnload(this.this$0, p_143375_);
    }
}
