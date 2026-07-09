package de.leoxian.moonlightcore.internal.common.mod.client;

import de.leoxian.moonlightcore.client.network.ClientConfigurationNetworking;
import de.leoxian.moonlightcore.client.network.ClientPlayNetworking;
import de.leoxian.moonlightcore.common.ModContainer;
import de.leoxian.moonlightcore.common.entrypoint.ClientModInitializer;
import de.leoxian.moonlightcore.internal.common.config.sync.s2c.S2CSyncLoadedConfigPacket;

public class InternalClientMod implements ClientModInitializer {
    @Override
    public void onInitializedClient(ModContainer container) {
        ClientConfigurationNetworking.register(S2CSyncLoadedConfigPacket.TYPE, S2CSyncLoadedConfigPacket.STREAM_CODEC, S2CSyncLoadedConfigPacket::handleConfiguration);
        ClientPlayNetworking.register(S2CSyncLoadedConfigPacket.TYPE, S2CSyncLoadedConfigPacket.STREAM_CODEC, S2CSyncLoadedConfigPacket::handlePlay);
    }
}
