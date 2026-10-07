package de.leoxian.moonlightcore.internal.core.client;

import de.leoxian.moonlightcore.client.network.ClientPlayNetworking;
import de.leoxian.moonlightcore.common.config.Config;
import de.leoxian.moonlightcore.common.config.file.LoadedConfig;
import de.leoxian.moonlightcore.common.util.DynamicRegistryUtils;
import de.leoxian.moonlightcore.internal.common.config.ConfigRegistry;
import de.leoxian.moonlightcore.internal.core.network.clientbound.*;
import de.leoxian.moonlightcore.internal.core.network.serverbound.ServerboundAcceptedConfigurationsPacketPayload;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;

public final class MoonlightCoreClient {
	public static void initializeClient() {
		initializeNetwork();
	}

	private static void initializeNetwork() {
		// Play
		ClientPlayNetworking.registerHandler(ClientboundSyncLoadedConfigPacketPayload.TYPE, MoonlightCoreClient::handlePlay);
		ClientPlayNetworking.registerHandler(ClientboundRequestValidConfigurationsPacketPayload.TYPE, MoonlightCoreClient::handlePlay);
		ClientPlayNetworking.registerHandler(ClientboundCreateDimensionPacketPayload.TYPE, MoonlightCoreClient::handlePlay);
		ClientPlayNetworking.registerHandler(ClientboundRemoveDynamicDimensionPacketPayload.TYPE, MoonlightCoreClient::handlePlay);
	}

	private static void handlePlay(ClientboundCreateDimensionPacketPayload packet, ClientPlayNetworking.Context context) {
		context.enqueueWork(() -> {
			ClientPacketListener packetListener = context.packetListener();
			Identifier id = packet.id();
			DimensionType dimensionType = packet.dimensionType();

			DynamicRegistryUtils.register(packetListener.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE), id, () -> dimensionType);
			packetListener.levels().add(ResourceKey.create(Registries.DIMENSION, id));
		});
	}

	private static void handlePlay(ClientboundRemoveDynamicDimensionPacketPayload packet, ClientPlayNetworking.Context context) {
		context.enqueueWork(() -> {
		Identifier id = packet.id();
		ClientPacketListener packetListener = context.packetListener();

		DynamicRegistryUtils.unregister(packetListener.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE), id);
			packetListener.levels().remove(ResourceKey.create(Registries.DIMENSION, id));
		});
	}

	private static void handlePlay(ClientboundSyncLoadedConfigPacketPayload packet, ClientPlayNetworking.Context context) {
		context.enqueueWork(() -> {
			Config<?> config = ConfigRegistry.getConfig(packet.config());
			LoadedConfig loadedConfig = packet.data();

			if (config != null) {
				config.loadedConfig().applyFrom(config.schema(), loadedConfig);
			}
		});
	}

	private static void handlePlay(ClientboundRequestValidConfigurationsPacketPayload packet, ClientPlayNetworking.Context context) {
		context.enqueueWork(() -> {
			context.responseSender().sendPacket(new ServerboundAcceptedConfigurationsPacketPayload(ConfigRegistry.getSyncableConfigs()));
		});
	}

	private MoonlightCoreClient() {}
}
