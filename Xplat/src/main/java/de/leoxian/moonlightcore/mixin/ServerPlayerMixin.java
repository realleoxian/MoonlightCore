package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.api.event.EventDispatcher;
import de.leoxian.moonlightcore.api.event.server.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    public void mlcore_restoreFrom(ServerPlayer that, boolean keepEverything, CallbackInfo ci) {
        EventDispatcher.INSTANCE.fire(ServerPlayerEvents.COPY_FROM, (listener) -> listener.bootstrap(that, (ServerPlayer) (Object) this, keepEverything));
    }

}
