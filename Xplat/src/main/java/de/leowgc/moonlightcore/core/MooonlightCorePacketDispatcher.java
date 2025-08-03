package de.leowgc.moonlightcore.core;

import de.leowgc.moonlightcore.api.network.PacketDispatcher;
import de.leowgc.moonlightcore.config.sync.ConfigSyncPacket;

public final class MooonlightCorePacketDispatcher extends PacketDispatcher {

    MooonlightCorePacketDispatcher() {
        super(MoonlightCore.prefix("main"));
    }

    @Override
    public void bootstrap() {
        this.registerClientBound(ConfigSyncPacket.ID, ConfigSyncPacket.class, ConfigSyncPacket.CODEC, ConfigSyncPacket::handle);
    }
}
