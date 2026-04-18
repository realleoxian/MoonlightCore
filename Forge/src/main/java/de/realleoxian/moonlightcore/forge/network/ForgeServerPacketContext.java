package de.realleoxian.moonlightcore.forge.network;

import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.network.PacketSender;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public record ForgeServerPacketContext(ServerPlayer serverPlayer, MinecraftServer server) implements NetworkHelper.PacketContext<ServerGamePacketListenerImpl> {
    @Override
    public void queueWork(Runnable task) {
        if (server.isSameThread()) {
            task.run();
            return;
        }

        server.submitAsync(task);
    }

    @Override
    public Player player() {
        return serverPlayer();
    }

    @Override
    public ServerGamePacketListenerImpl handler() {
        return serverPlayer().connection;
    }

    @Override
    public Connection connection() {
        return serverPlayer().connection.connection;
    }

    @Override
    public PacketSender packetSender() {
        return PacketSender.ofPlayer(serverPlayer());
    }

    @Override
    public EnvSide getReceptionSide() {
        return EnvSide.SERVER;
    }
}
