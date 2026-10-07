package de.leoxian.moonlightcore.internal.core;

import com.mojang.logging.LogUtils;
import de.leoxian.moonlightcore.common.config.Config;
import de.leoxian.moonlightcore.common.event.ServerPlayConnectionEvents;
import de.leoxian.moonlightcore.common.network.PayloadTypeRegister;
import de.leoxian.moonlightcore.common.network.ServerPlayNetworking;
import de.leoxian.moonlightcore.internal.common.config.ConfigRegistry;
import de.leoxian.moonlightcore.internal.core.network.clientbound.*;
import de.leoxian.moonlightcore.internal.core.network.serverbound.ServerboundAcceptedConfigurationsPacketPayload;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class MoonlightCore {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static Identifier id(String id) {
		return Identifier.fromNamespaceAndPath("moonlightcore", id);
	}

	public static void init() {
		initializeNetwork();
	}

	private static void initializeNetwork() {
		// Clientbound
		PayloadTypeRegister.clientboundPlay(ClientboundSyncLoadedConfigPacketPayload.TYPE, ClientboundSyncLoadedConfigPacketPayload.STREAM_CODEC);
		PayloadTypeRegister.clientboundPlay(ClientboundRequestValidConfigurationsPacketPayload.TYPE, ClientboundRequestValidConfigurationsPacketPayload.STREAM_CODEC);
		PayloadTypeRegister.clientboundPlay(ClientboundCreateDimensionPacketPayload.TYPE, ClientboundCreateDimensionPacketPayload.STREAM_CODEC);
		PayloadTypeRegister.clientboundPlay(ClientboundRemoveDynamicDimensionPacketPayload.TYPE, ClientboundRemoveDynamicDimensionPacketPayload.STREAM_CODEC);

		// Serverbound
		PayloadTypeRegister.serverboundPlay(ServerboundAcceptedConfigurationsPacketPayload.TYPE, ServerboundAcceptedConfigurationsPacketPayload.STREAM_CODEC);
		ServerPlayNetworking.registerHandler(ServerboundAcceptedConfigurationsPacketPayload.TYPE, MoonlightCore::handle);

		ServerPlayConnectionEvents.JOIN.subscribe((packetListener, sender) -> {
			if (!ServerPlayNetworking.canSendToPlayer(packetListener.player, ClientboundRequestValidConfigurationsPacketPayload.TYPE)) {
				return;
			}
			sender.sendPacket(ClientboundRequestValidConfigurationsPacketPayload.INSTANCE);
		});
	}

	private static void handle(ServerboundAcceptedConfigurationsPacketPayload packet, ServerPlayNetworking.Context context) {
		context.enqueueWork(() -> {
			Set<Identifier> decoded = decodeSyncableConfigs(packet);

			for (final Identifier syncable : decoded) {
				Config<?> config = ConfigRegistry.getConfig(syncable);
				if (config == null) {
					continue;
				}

				context.responseSender().sendPacket(new ClientboundSyncLoadedConfigPacketPayload(syncable, config.loadedConfig()));
			}
		});
	}

	private static Set<Identifier> decodeSyncableConfigs(ServerboundAcceptedConfigurationsPacketPayload packet) {
		Set<Identifier> clientValidConfigs = new HashSet<>(packet.acceptedConfigurations());
		Set<Identifier> serverValidConfigs = ConfigRegistry.getSyncableConfigs();
		clientValidConfigs.retainAll(serverValidConfigs);

		if (clientValidConfigs.size() < serverValidConfigs.size()) {
			LOGGER.warn("Client doesn't support all mod configurations.");
			LOGGER.warn("   - Client: {}", clientValidConfigs.size());
			LOGGER.warn("   - Server: {}", serverValidConfigs.size());
			LOGGER.warn("Missing server configurations on the client:");
			LOGGER.warn(serverValidConfigs.stream()
					.filter(id -> !clientValidConfigs.contains(id))
					.map(Identifier::toString)
					.collect(Collectors.joining(", ")));
		}
		return clientValidConfigs;
	}

	private MoonlightCore() {}
}
