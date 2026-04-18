package de.realleoxian.moonlightcore.fabric.network;

import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.network.PacketSender;
import de.realleoxian.moonlightcore.mixin.BlockableEventLoopInvoker;
import de.realleoxian.moonlightcore.fabric.mixin.ServerGamePacketListenerImplAccessor;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public record FabricServerPacketContext(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler) implements NetworkHelper.PacketContext<ServerGamePacketListenerImpl> {
    @Override
    public void queueWork(Runnable task) {
        if (server.isSameThread()) {
            task.run();
            return;
        }

        ((BlockableEventLoopInvoker) server).submitAsync(task);
    }

    @Override
    public Connection connection() {
        return ((ServerGamePacketListenerImplAccessor) handler).getConnection();
    }

    @Override
    public PacketSender packetSender() {
        return PacketSender.ofPlayer(player);
    }

    @Override
    public EnvSide getReceptionSide() {
        return EnvSide.SERVER;
    }
}
