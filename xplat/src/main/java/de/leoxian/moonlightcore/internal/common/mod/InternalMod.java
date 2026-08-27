package de.leoxian.moonlightcore.internal.common.mod;

import de.leoxian.moonlightcore.common.event.RegisterConfigurationTasksEvent;
import de.leoxian.moonlightcore.common.event.base.EventPriority;
import de.leoxian.moonlightcore.common.network.ServerConfigurationNetworking;
import de.leoxian.moonlightcore.common.network.ServerPlayNetworking;
import de.leoxian.moonlightcore.internal.common.network.c2s.C2SAcceptedValidConfigs;
import de.leoxian.moonlightcore.internal.common.network.s2c.S2CRequestValidConfigsPacket;
import de.leoxian.moonlightcore.internal.common.network.task.RequestValidConfigsTask;

public class InternalMod {
    public static void initialize() {
        ServerConfigurationNetworking.register(C2SAcceptedValidConfigs.TYPE, C2SAcceptedValidConfigs.STREAM_CODEC, C2SAcceptedValidConfigs::handleConfiguration);
        ServerPlayNetworking.register(C2SAcceptedValidConfigs.TYPE, C2SAcceptedValidConfigs.STREAM_CODEC, C2SAcceptedValidConfigs::handlePlay);

        RegisterConfigurationTasksEvent.EVENT.subscribe(EventPriority.HIGHEST, (packetListener, server) -> {
            if (ServerConfigurationNetworking.canSend(packetListener, S2CRequestValidConfigsPacket.TYPE)) {
                ServerConfigurationNetworking.addTask("moonlightcore", packetListener, new RequestValidConfigsTask(packetListener));
            }
        });
    }
}
