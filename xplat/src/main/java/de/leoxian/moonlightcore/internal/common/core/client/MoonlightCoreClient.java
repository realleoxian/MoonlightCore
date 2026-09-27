package de.leoxian.moonlightcore.internal.common.core.client;

import de.leoxian.moonlightcore.client.network.ClientConfigurationNetworking;
import de.leoxian.moonlightcore.client.network.ClientPlayNetworking;
import de.leoxian.moonlightcore.internal.common.network.s2c.*;

public final class MoonlightCoreClient {
	public static void initializeClientMod() {
		// -- Configuration --
		ClientConfigurationNetworking.register(S2CRequestValidConfigsPacket.TYPE, S2CRequestValidConfigsPacket.STREAM_CODEC, S2CRequestValidConfigsPacket::handleConfiguration);
		ClientConfigurationNetworking.register(S2CSyncLoadedConfigPacket.TYPE, S2CSyncLoadedConfigPacket.STREAM_CODEC, S2CSyncLoadedConfigPacket::handleConfiguration);
		ClientConfigurationNetworking.register(S2CNotifyConfigSyncFinishPacket.TYPE, S2CNotifyConfigSyncFinishPacket.STREAM_CODEC, S2CNotifyConfigSyncFinishPacket::handle);

		// -- Play --
		ClientPlayNetworking.register(S2CRequestValidConfigsPacket.TYPE, S2CRequestValidConfigsPacket.STREAM_CODEC, S2CRequestValidConfigsPacket::handlePlay);
		ClientPlayNetworking.register(S2CSyncLoadedConfigPacket.TYPE, S2CSyncLoadedConfigPacket.STREAM_CODEC, S2CSyncLoadedConfigPacket::handlePlay);
		ClientPlayNetworking.register(S2CRemoveDimensionPacket.TYPE, S2CRemoveDimensionPacket.STREAM_CODEC, S2CRemoveDimensionPacket::handle);
		ClientPlayNetworking.register(S2CCreateDimension.TYPE, S2CCreateDimension.STREAM_CODEC, S2CCreateDimension::handle);
	}

	private MoonlightCoreClient() {}
}
