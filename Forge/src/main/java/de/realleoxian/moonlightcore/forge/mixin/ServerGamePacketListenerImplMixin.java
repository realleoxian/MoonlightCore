package de.realleoxian.moonlightcore.forge.mixin;

import de.realleoxian.moonlightcore.forge.network.ServerPacketHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 999)
public class ServerGamePacketListenerImplMixin {
    @Inject(
            method = "<init>",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$init(MinecraftServer server, Connection connection, ServerPlayer player, CallbackInfo ci) {
        ServerPacketHandler.setContext((ServerGamePacketListenerImpl) (Object) this, server);
    }

    @Inject(
            method = "onDisconnect",
            at = @At(value = "HEAD")
    )
    private void moonlightcore$onDisconnect(Component reason, CallbackInfo ci) {
        ServerPacketHandler.dispatchLOggedOutEvent();
    }

    @Inject(
            method = "onDisconnect",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$onDisconnectEnd(Component reason, CallbackInfo ci) {
        ServerPacketHandler.invalidate();
    }
}
