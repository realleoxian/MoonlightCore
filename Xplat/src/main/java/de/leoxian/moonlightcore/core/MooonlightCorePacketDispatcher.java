package de.leoxian.moonlightcore.core;

import de.leoxian.moonlightcore.api.network.PacketDispatcher;
import de.leoxian.moonlightcore.config.sync.ConfigSyncPacket;

public final class MooonlightCorePacketDispatcher extends PacketDispatcher {

    MooonlightCorePacketDispatcher() {
        super(MoonlightCore.prefix("main"));
    }

    @Override
    public void bootstrap() {
        this.registerClientBound(ConfigSyncPacket.ID, ConfigSyncPacket.class, ConfigSyncPacket.CODEC, ConfigSyncPacket::handle);
    }
}
