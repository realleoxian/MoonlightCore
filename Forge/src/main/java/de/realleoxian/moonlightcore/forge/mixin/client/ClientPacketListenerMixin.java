package de.realleoxian.moonlightcore.forge.mixin.client;

import de.realleoxian.moonlightcore.api.client.event.ClientPlayerNetworkEvents;
import de.realleoxian.moonlightcore.api.network.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = "handleLogin",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$handleLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
        try {
            ClientPlayerNetworkEvents.LOGGED_IN.invoker().onPlayerLoggedIn((ClientPacketListener) (Object) this, PacketSender.client(), this.minecraft);
        } catch (Exception e) {
            throw new RuntimeException("Exception encountered on ClientPlayerNetworkEvents$LOGGED_IN", e);
        }
    }

    @Inject(
            method = "handleDisconnect",
            at = @At(value = "HEAD")
    )
    private void moonlightcore$handleDisconnect(ClientboundDisconnectPacket packet, CallbackInfo ci) {
        ClientPlayerNetworkEvents.LOGGED_OUT.invoker().onPlayerLoggedOut((ClientPacketListener) (Object) this, this.minecraft);
    }
}
