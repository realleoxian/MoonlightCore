package de.realleoxian.moonlightcore.forge.client.network;

import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.network.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.world.entity.player.Player;

public final class ForgeClientPacketContext implements NetworkHelper.PacketContext {
    public static final NetworkHelper.PacketContext INSTANCE = new ForgeClientPacketContext();

    private ForgeClientPacketContext() {}

    @Override
    public void queueWork(Runnable task) {
        if (Minecraft.getInstance().isSameThread()) task.run();
        else Minecraft.getInstance().execute(task);
    }

    @Override
    public Player player() {
        Player player = Minecraft.getInstance().player;
        if (player == null)
            throw new IllegalStateException("Cannot retrieve client player because it is 'null' (weird (???))");

        return player;
    }

    @Override
    public Connection handler() {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        if (listener == null)
            throw new IllegalStateException("For some weird reason client packet listener it's null..?");

        return listener.getConnection();
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
