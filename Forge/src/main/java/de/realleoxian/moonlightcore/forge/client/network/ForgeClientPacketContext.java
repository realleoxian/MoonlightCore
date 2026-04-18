package de.realleoxian.moonlightcore.forge.client.network;

import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.network.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.world.entity.player.Player;

public final class ForgeClientPacketContext implements NetworkHelper.PacketContext<ClientPacketListener> {
    public static final ForgeClientPacketContext INSTANCE = new ForgeClientPacketContext();

    @Override
    public void queueWork(Runnable task) {
        if (Minecraft.getInstance().isSameThread()) {
            task.run();
            return;
        }

        Minecraft.getInstance().submitAsync(task);
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

    private ForgeClientPacketContext() {}
}
