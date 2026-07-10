package de.leoxian.moonlightcore.internal.common.mod;

import de.leoxian.moonlightcore.common.ModContainer;
import de.leoxian.moonlightcore.common.entrypoint.ModInitializer;
import de.leoxian.moonlightcore.common.event.ServerConfigurationConnectionEvents;
import de.leoxian.moonlightcore.common.event.base.EventPriority;
import de.leoxian.moonlightcore.common.network.ServerConfigurationNetworking;
import de.leoxian.moonlightcore.common.network.ServerPlayNetworking;
import de.leoxian.moonlightcore.internal.common.config.ConfigRegistry;
import de.leoxian.moonlightcore.internal.common.config.sync.c2s.C2SAcceptedValidConfigs;
import de.leoxian.moonlightcore.internal.common.config.sync.s2c.S2CRequestValidConfigsPacket;
import de.leoxian.moonlightcore.internal.common.config.sync.task.RequestValidConfigsTask;
import de.leoxian.moonlightcore.internal.common.config.sync.task.SyncConfigurationTask;
import net.minecraft.resources.Identifier;

import java.util.Set;

public class InternalMod implements ModInitializer {
    @Override
    public void onInitialized(ModContainer container) {
        ServerConfigurationNetworking.register(C2SAcceptedValidConfigs.TYPE, C2SAcceptedValidConfigs.STREAM_CODEC, C2SAcceptedValidConfigs::handleConfiguration);
        ServerPlayNetworking.register(C2SAcceptedValidConfigs.TYPE, C2SAcceptedValidConfigs.STREAM_CODEC, C2SAcceptedValidConfigs::handlePlay);

        ServerConfigurationConnectionEvents.CONFIGURE.subscribe(EventPriority.HIGHEST, (packetListener, server) -> {
            if (ServerConfigurationNetworking.canSend(packetListener, S2CRequestValidConfigsPacket.TYPE)) {
                ServerConfigurationNetworking.addTask(packetListener, new RequestValidConfigsTask(packetListener));
            }
        });
    }
}
