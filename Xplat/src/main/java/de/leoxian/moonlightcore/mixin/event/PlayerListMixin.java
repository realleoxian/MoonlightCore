package de.leoxian.moonlightcore.mixin.event;

import de.leoxian.moonlightcore.event.common.PlayerEvent;
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

    @Inject(method = "placeNewPlayer", at = @At("RETURN"))
    private void mlcore_placeNewPlayer(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        PlayerEvent.JOIN_SERVER.invoker().onPlayerJoin(player, player.server);
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void mlcore_remove(ServerPlayer player, CallbackInfo ci) {
        PlayerEvent.QUIT_SERVER.invoker().onPlayerQuit(player, player.server);
    }

    @Inject(method = "respawn", at = @At("RETURN"))
    private void mlcore_respawn(ServerPlayer player, boolean keepEverything, CallbackInfoReturnable<ServerPlayer> cir) {
        PlayerEvent.AFTER_RESPAWN.invoker().onPlayerRespawn(player, cir.getReturnValue(), keepEverything);
    }
}
