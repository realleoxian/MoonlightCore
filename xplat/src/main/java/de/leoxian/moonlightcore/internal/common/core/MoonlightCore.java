package de.leoxian.moonlightcore.internal.common.core;

import com.mojang.logging.LogUtils;
import de.leoxian.moonlightcore.common.event.ServerConfigurationConnectionEvents;
import de.leoxian.moonlightcore.common.event.base.EventPriority;
import de.leoxian.moonlightcore.common.network.ServerConfigurationNetworking;
import de.leoxian.moonlightcore.common.network.ServerPlayNetworking;
import de.leoxian.moonlightcore.internal.common.network.c2s.C2SAcceptedValidConfigs;
import de.leoxian.moonlightcore.internal.common.network.s2c.S2CRequestValidConfigsPacket;
import de.leoxian.moonlightcore.internal.common.network.task.RequestValidConfigsTask;
import org.slf4j.Logger;

public class MoonlightCore {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void initialize() {
        ServerConfigurationNetworking.register(C2SAcceptedValidConfigs.TYPE, C2SAcceptedValidConfigs.STREAM_CODEC, C2SAcceptedValidConfigs::handleConfiguration);
        ServerPlayNetworking.register(C2SAcceptedValidConfigs.TYPE, C2SAcceptedValidConfigs.STREAM_CODEC, C2SAcceptedValidConfigs::handlePlay);

        ServerConfigurationConnectionEvents.BEFORE_CONFIGURE.subscribe((listener, server, tasksSender, taskFinisher) -> {
            if (ServerConfigurationNetworking.canSend(listener, S2CRequestValidConfigsPacket.TYPE)) {
                tasksSender.accept(new RequestValidConfigsTask());
            }
        });
    }
}
