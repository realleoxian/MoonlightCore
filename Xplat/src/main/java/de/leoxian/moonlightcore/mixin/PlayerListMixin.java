package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.api.event.EventDispatcher;
import de.leoxian.moonlightcore.api.event.server.ServerPlayerEvents;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    public void mlcore_placeNewPlayer(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        EventDispatcher.INSTANCE.fire(ServerPlayerEvents.JOIN_SERVER, (listener) -> listener.bootstrap(player.getServer(), player));
    }

    @Inject(method = "remove", at = @At("TAIL"))
    public void mlcore_remove(ServerPlayer player, CallbackInfo ci) {
        EventDispatcher.INSTANCE.fire(ServerPlayerEvents.LEAVE_SERVER, (listener) -> listener.bootstrap(player.getServer()));
    }

    @Inject(method = "respawn", at = @At("TAIL"))
    public void mlcore_respawn(ServerPlayer player, boolean keepEverything, CallbackInfoReturnable<ServerPlayer> cir) {
        EventDispatcher.INSTANCE.fire(ServerPlayerEvents.AFTER_RESPAWN, (listener) -> listener.bootstrap(player, cir.getReturnValue(), keepEverything));
    }

}
