package de.leoxian.moonlightcore.internal.common.mod.client;

import de.leoxian.moonlightcore.client.network.ClientConfigurationNetworking;
import de.leoxian.moonlightcore.client.network.ClientPlayNetworking;
import de.leoxian.moonlightcore.internal.common.network.s2c.S2CCreateDimension;
import de.leoxian.moonlightcore.internal.common.network.s2c.S2CRemoveDimensionPacket;
import de.leoxian.moonlightcore.internal.common.network.s2c.S2CRequestValidConfigsPacket;
import de.leoxian.moonlightcore.internal.common.network.s2c.S2CSyncLoadedConfigPacket;

public final class InternalClientMod {
    public static void initializeClientMod() {
        ClientConfigurationNetworking.register(S2CRequestValidConfigsPacket.TYPE, S2CRequestValidConfigsPacket.STREAM_CODEC, S2CRequestValidConfigsPacket::handleConfiguration);
        ClientPlayNetworking.register(S2CRequestValidConfigsPacket.TYPE, S2CRequestValidConfigsPacket.STREAM_CODEC, S2CRequestValidConfigsPacket::handlePlay);

        ClientConfigurationNetworking.register(S2CSyncLoadedConfigPacket.TYPE, S2CSyncLoadedConfigPacket.STREAM_CODEC, S2CSyncLoadedConfigPacket::handleConfiguration);
        ClientPlayNetworking.register(S2CSyncLoadedConfigPacket.TYPE, S2CSyncLoadedConfigPacket.STREAM_CODEC, S2CSyncLoadedConfigPacket::handlePlay);

        ClientPlayNetworking.register(S2CRemoveDimensionPacket.TYPE, S2CRemoveDimensionPacket.STREAM_CODEC, S2CRemoveDimensionPacket::handle);
        ClientPlayNetworking.register(S2CCreateDimension.TYPE, S2CCreateDimension.STREAM_CODEC, S2CCreateDimension::handle);
    }

    private InternalClientMod() {}
}
