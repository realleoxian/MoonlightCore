package de.leoxian.moonlightcore.fabric.common.mixin.event;

import de.leoxian.moonlightcore.common.event.OnDatapackSyncEvent;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(
            method = "placeNewPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;getRecipeManager()Lnet/minecraft/world/item/crafting/RecipeManager;",
                    shift = At.Shift.BEFORE
            )
    )
    private void moonlightcore$dispatchOnDatapackSyncPacket(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        OnDatapackSyncEvent.EVENT.doFire().onDatapackSync((PlayerList) (Object) this, player);
    }

    @Inject(
            method = "reloadResources",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void moonlightcore$dispatchOnDatapackSyncPacket(CallbackInfo ci) {
        OnDatapackSyncEvent.EVENT.doFire().onDatapackSync((PlayerList) (Object) this, null);
    }
}
