package de.realleoxian.moonlightcore.forge.network;

import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.network.PacketSender;
import net.minecraft.network.Connection;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public record ForgeServerPacketContext(NetworkEvent.Context forgeCtx) implements NetworkHelper.PacketContext {
    @Override
    public void queueWork(Runnable task) {
        forgeCtx.enqueueWork(task);
    }

    @Override
    public Connection handler() {
        return forgeCtx.getNetworkManager();
    }

    @Override
    public Player player() {
        return forgeCtx.getSender();
    }

    @Override
    public PacketSender packetSender() {
        return PacketSender.ofPlayer(forgeCtx.getSender());
    }

    @Override
    public EnvSide getReceptionSide() {
        return EnvSide.SERVER;
    }
}
