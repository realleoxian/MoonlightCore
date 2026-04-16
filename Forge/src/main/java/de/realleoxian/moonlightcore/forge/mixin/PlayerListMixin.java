package de.realleoxian.moonlightcore.forge.mixin;

import de.realleoxian.moonlightcore.api.event.ServerPlayerEvents;
import de.realleoxian.moonlightcore.forge.network.ServerPacketHandler;
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
    @Inject(
            method = "placeNewPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/game/ClientboundCustomPayloadPacket;<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/network/FriendlyByteBuf;)V"
            )
    )
    private void moonlightcore$placeNewPlayer(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        ServerPacketHandler.dispatchLoggedInEvent(player);
    }

    @Inject(
            method = "respawn",
            at = @At(value = "TAIL")
    )
    private void moonlightcore$respawn(ServerPlayer player, boolean keepEverything, CallbackInfoReturnable<ServerPlayer> cir) {
        ServerPlayerEvents.AFTER_RESPAWN.invoker().onPlayerRespawn(player, cir.getReturnValue());
    }
}
