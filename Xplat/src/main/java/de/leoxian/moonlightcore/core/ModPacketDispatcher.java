package de.leoxian.moonlightcore.core;

import de.leoxian.moonlightcore.api.network.PacketDispatcher;

public final class ModPacketDispatcher extends PacketDispatcher {

    ModPacketDispatcher() {
        super(MoonlightCore.prefix("main"));
    }

    @Override
    public void bootstrap() {

    }
}
