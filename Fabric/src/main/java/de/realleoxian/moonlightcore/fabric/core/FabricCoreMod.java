package de.realleoxian.moonlightcore.fabric.core;

import de.realleoxian.moonlightcore.api.MoonlightCore;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.fabric.core.network.c2s.C2SModListCheckPacket;

public class FabricCoreMod {
    public static void initialize() {
        var registrar = MoonlightCore.getNetworkHelper().handlerThread("moonlightcore", NetworkHelper.HandlerThread.MAIN).registrar("moonlightcore");
        registrar.serverbound(C2SModListCheckPacket.TYPE, C2SModListCheckPacket::handle);
    }
}
