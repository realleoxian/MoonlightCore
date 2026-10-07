package de.leoxian.moonlightcore.neoforge.common.network;

import de.leoxian.moonlightcore.common.network.ServerConfigurationNetworking;
import de.leoxian.moonlightcore.common.network.ServerPlayNetworking;
import de.leoxian.moonlightcore.neoforge.common.hooks.ModEventBusRegistrable;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NeoforgeNetworkHandler implements ModEventBusRegistrable {
	private final Map<CustomPacketPayload.Type<?>, ServerboundEntry<?>> serverboundEntries = new ConcurrentHashMap<>();
	private final Map<CustomPacketPayload.Type<?>, ClientboundEntry<?>> clientboundEntries = new ConcurrentHashMap<>();

	@Override
	public void register(IEventBus modEventBus) {
		modEventBus.addListener((RegisterPayloadHandlersEvent event) -> {
			final PayloadRegistrar registrar = event.registrar("1");

			for (ServerboundEntry<?> entry : this.serverboundEntries.values()) {
				entry.register(registrar);
			}
			for (ClientboundEntry<?> entry : this.clientboundEntries.values()) {
				entry.register(registrar);
			}
		});
	}

	public <MSG extends CustomPacketPayload> void serverboundPlay(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> streamCodec) {
		ServerboundEntry<MSG> entry = getOrCreateServerboundEntry(type);
		entry.playCodec = streamCodec;
	}

	public <MSG extends CustomPacketPayload> void clientboundPlay(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> streamCodec) {
		getOrCreateClientboundEntry(type).playCodec = streamCodec;
	}

	public <MSG extends CustomPacketPayload> void serverboundPlayHandler(CustomPacketPayload.Type<MSG> type, ServerPlayNetworking.Handler<MSG> handler) {
		ServerboundEntry<MSG> entry = getOrCreateServerboundEntry(type);
		entry.playHandler = handler;
	}

	public <MSG extends CustomPacketPayload> void serverboundConfiguration(CustomPacketPayload.Type<MSG> type, StreamCodec<? super FriendlyByteBuf, MSG> streamCodec) {
		ServerboundEntry<MSG> entry = getOrCreateServerboundEntry(type);
		entry.configCodec = streamCodec;
	}

	public <MSG extends CustomPacketPayload> void serverboundConfigurationHandler(CustomPacketPayload.Type<MSG> type, ServerConfigurationNetworking.Handler<MSG> handler) {
		ServerboundEntry<MSG> entry = getOrCreateServerboundEntry(type);
		entry.configHandler = handler;
	}

	public <MSG extends CustomPacketPayload> void clientboundConfiguration(CustomPacketPayload.Type<MSG> type, StreamCodec<? super FriendlyByteBuf, MSG> streamCodec) {
		getOrCreateClientboundEntry(type).configCodec = streamCodec;
	}

	@SuppressWarnings("unchecked")
	private <MSG extends CustomPacketPayload> ServerboundEntry<MSG> getOrCreateServerboundEntry(CustomPacketPayload.Type<MSG> type) {
		return (ServerboundEntry<MSG>) this.serverboundEntries.computeIfAbsent(type, ServerboundEntry::new);
	}

	@SuppressWarnings("unchecked")
	private <MSG extends CustomPacketPayload> ClientboundEntry<MSG> getOrCreateClientboundEntry(CustomPacketPayload.Type<MSG> type) {
		return (ClientboundEntry<MSG>) this.clientboundEntries.computeIfAbsent(type, ClientboundEntry::new);
	}

	private static class ServerboundEntry<MSG extends CustomPacketPayload> {
		private final CustomPacketPayload.Type<MSG> type;

		private StreamCodec<? super RegistryFriendlyByteBuf, MSG> playCodec = null;
		private ServerPlayNetworking.Handler<MSG> playHandler = null;
		private StreamCodec<? super FriendlyByteBuf, MSG> configCodec = null;
		private ServerConfigurationNetworking.Handler<MSG> configHandler = null;

		private ServerboundEntry(CustomPacketPayload.Type<MSG> type) {
			this.type = type;
		}

		public void register(final PayloadRegistrar registrar) {
			if ((this.playCodec != null && this.playHandler != null) && (this.configCodec != null && this.configHandler != null)) {
				// It needs a FriendlyByteBuf
				registrar.commonToServer(this.type, this.configCodec, (payload, context) -> {
					ConnectionProtocol protocol = context.protocol();

					// Use a different handler depending on the protocol
					switch (protocol) {
						case PLAY -> this.playHandler.handle(payload, new NeoforgeServerPlayContext(context, (ServerPlayer) context.player()));
						case CONFIGURATION -> this.configHandler.handle(payload, new NeoforgeServerConfigurationContext(context));
						default -> {}
					}
				});
			} else if (this.playCodec != null && this.playHandler != null) {
				registrar.playToServer(this.type, this.playCodec, (payload, context) -> {
					this.playHandler.handle(payload, new NeoforgeServerPlayContext(context, (ServerPlayer) context.player()));
				});
			} else if (this.configCodec != null && this.configHandler != null) {
				registrar.configurationToServer(this.type, this.configCodec, (payload, context) -> {
					this.configHandler.handle(payload, new NeoforgeServerConfigurationContext(context));
				});
			}
		}
	}

	private static class ClientboundEntry<MSG extends CustomPacketPayload> {
		private final CustomPacketPayload.Type<MSG> type;
		private StreamCodec<? super RegistryFriendlyByteBuf, MSG> playCodec;
		private StreamCodec<? super FriendlyByteBuf, MSG> configCodec;

		private ClientboundEntry(CustomPacketPayload.Type<MSG> type) {
			this.type = type;
		}

		public void register(PayloadRegistrar registrar) {
			if (this.playCodec != null && this.configCodec != null) {
				registrar.commonToClient(this.type, this.configCodec);
			} else if (this.playCodec != null) {
				registrar.playToClient(this.type, this.playCodec);
			} else if (this.configCodec != null) {
				registrar.configurationToClient(this.type, this.configCodec);
			}
		}
	}
}
