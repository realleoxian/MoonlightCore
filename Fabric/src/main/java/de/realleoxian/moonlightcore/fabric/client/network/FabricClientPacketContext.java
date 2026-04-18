package de.realleoxian.moonlightcore.fabric.client.network;

import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.network.PacketSender;
import de.realleoxian.moonlightcore.mixin.BlockableEventLoopInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.world.entity.player.Player;

public enum FabricClientPacketContext implements NetworkHelper.PacketContext<ClientPacketListener> {
    INSTANCE
    ;

    @Override
    public void queueWork(Runnable task) {
        if (Minecraft.getInstance().isSameThread()) {
            task.run();
            return;
        }

        ((BlockableEventLoopInvoker) Minecraft.getInstance()).submitAsync(task);
    }

    @Override
    public Player player() {
        return Minecraft.getInstance().player;
    }

    @Override
    public ClientPacketListener handler() {
        return Minecraft.getInstance().getConnection();
    }

    @Override
    public Connection connection() {
        return handler().getConnection();
    }

    @Override
    public PacketSender packetSender() {
        return PacketSender.client();
    }

    @Override
    public EnvSide getReceptionSide() {
        return EnvSide.CLIENT;
    }
}
