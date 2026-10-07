package de.leoxian.moonlightcore.neoforge.client.network;

import de.leoxian.moonlightcore.client.network.ClientConfigurationNetworking;
import de.leoxian.moonlightcore.client.network.ClientPlayNetworking;
import de.leoxian.moonlightcore.neoforge.common.hooks.ModEventBusRegistrable;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.*;

public class NeoforgeClientNetworkHandler implements ModEventBusRegistrable {
	private final Map<CustomPacketPayload.Type<?>, EnumMap<ConnectionProtocol, IPayloadHandler<?>>> handlers = new HashMap<>();

	@Override
	public void register(IEventBus modEventBus) {
		modEventBus.addListener((RegisterClientPayloadHandlersEvent event) -> {
			this.handlers.forEach((type, handlerByProtocol) -> {
				_registerClientPayload(event, type, handlerByProtocol);
			});
		});
	}

	@SuppressWarnings("unchecked")
	private <MSG extends CustomPacketPayload> void _registerClientPayload(RegisterClientPayloadHandlersEvent event, CustomPacketPayload.Type<MSG> type, EnumMap<ConnectionProtocol, IPayloadHandler<?>> map) {
		event.register(type, (payload, context) -> {
			ConnectionProtocol protocol = context.protocol();
			IPayloadHandler<MSG> handler = (IPayloadHandler<MSG>) map.get(protocol);

			if (handler != null) {
				handler.handle(payload, context);
			}
		});
	}

	public <MSG extends CustomPacketPayload> void playHandler(CustomPacketPayload.Type<MSG> type, ClientPlayNetworking.Handler<MSG> handler) {
		this.handlers.computeIfAbsent(type, k -> new EnumMap<>(ConnectionProtocol.class))
				.put(ConnectionProtocol.PLAY, (payload, context) -> {
					handler.handle((MSG) payload, new NeoforgeClientPlayNetworkingContext(context));
				});
	}

	public <MSG extends CustomPacketPayload> void configurationHandler(CustomPacketPayload.Type<MSG> type, ClientConfigurationNetworking.Handler<MSG> handler) {
		this.handlers.computeIfAbsent(type, k -> new EnumMap<>(ConnectionProtocol.class))
				.put(ConnectionProtocol.CONFIGURATION, (payload, context) -> {
					handler.handle((MSG) payload, new NeoforgeClientConfigurationNetworkingContext(context));
				});
	}
}
