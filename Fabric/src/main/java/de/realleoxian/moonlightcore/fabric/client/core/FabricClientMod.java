package de.realleoxian.moonlightcore.fabric.client.core;

import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.MoonlightCore;
import de.realleoxian.moonlightcore.api.client.event.ClientPlayerNetworkEvents;
import de.realleoxian.moonlightcore.api.event.EventPriority;
import de.realleoxian.moonlightcore.fabric.core.network.c2s.C2SModListCheckPacket;
import de.realleoxian.moonlightcore.fabric.network.FabricNetworkHelperImpl;

import java.util.HashMap;

public class FabricClientMod {
    public static void initializeClient() {
        ClientPlayerNetworkEvents.LOGGED_IN.subscribe(EventPriority.HIGHEST, (handler, sender, client) -> {
            var networkHelper = (FabricNetworkHelperImpl) MoonlightCore.getNetworkHelper();

            var channelVersions = new HashMap<String, FabricNetworkHelperImpl.ChannelVersion>();
            for (var namespace : networkHelper.getRegisteredMods()) {
                networkHelper.getChannelVersion(namespace, EnvSide.CLIENT).ifPresent(clientVersion -> channelVersions.put(namespace, clientVersion));
            }

            networkHelper.sendToServer(new C2SModListCheckPacket(channelVersions));
        });
    }
}
