package de.realleoxian.moonlightcore.xplat.client.internal;

import com.mojang.logging.LogUtils;
import de.realleoxian.moonlightcore.api.client.ClientModContainer;
import de.realleoxian.moonlightcore.api.client.network.ClientNetworking;
import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.xplat.config.file.ConfigTracker;
import de.realleoxian.moonlightcore.xplat.internal.network.clientbound.S2CRequestAcceptedModConfigsPacket;
import de.realleoxian.moonlightcore.xplat.internal.network.clientbound.S2CSyncConfigSchemaPacket;
import de.realleoxian.moonlightcore.xplat.internal.network.serverbound.C2SAcceptedModConfigsPacket;
import org.slf4j.Logger;

public final class InternalClientMod {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void initializeClient(ClientModContainer container) {
        setupConfig();
    }

    private static void setupConfig() {
        ClientNetworking.registerConfigurationPayload(S2CRequestAcceptedModConfigsPacket.TYPE, S2CRequestAcceptedModConfigsPacket.STREAM_CODEC, (networkHandler, minecraft, responseSender, payload) -> responseSender.sendPacket(new C2SAcceptedModConfigsPacket(ConfigTracker.getSyncableConfigs())));

        ClientNetworking.registerConfigurationPayload(S2CSyncConfigSchemaPacket.TYPE, S2CSyncConfigSchemaPacket.STREAM_CODEC, (networkHandler, minecraft, responseSender, payload) -> {
            final var modConfig = ConfigTracker.getConfig(ModConfig.Type.SERVER, payload.configName());
            if (modConfig == null) {
                LOGGER.warn("Received config sync unknown config: {}", payload.configName());
                return;
            }

            for (final var syncChange : payload.changes()) {
                try {
                    syncChange.tryApply(modConfig);
                } catch (Exception e) {
                    LOGGER.error("Failed to apply config change for key: {}", syncChange.configValueKey(), e);
                }
            }
        });
    }

    private InternalClientMod() {}
}
