package de.leoxian.moonlightcore.neoforge.client.network;

import de.leoxian.moonlightcore.client.network.ClientConfigurationNetworking;
import de.leoxian.moonlightcore.client.network.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class NeoforgeClientNetworkHandler {
    private final Map<CustomPacketPayload.Type<?>, PayloadPlayRegistration<?>> playPayloads = new ConcurrentHashMap<>();
    private final Map<CustomPacketPayload.Type<?>, PayloadConfigurationRegistration<?>> configurationPayloads = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onRegisterClientPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        if (!this.playPayloads.isEmpty())
            this.playPayloads.values().forEach(r -> r.register(registrar));

        if (!this.configurationPayloads.isEmpty())
            this.configurationPayloads.values().forEach(r -> r.register(registrar));
    }

    public <T extends CustomPacketPayload> void registerPlay(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, ClientPlayNetworking.Handler<T> handler) {
        if (this.playPayloads.putIfAbsent(type, new PayloadPlayRegistration<>(type, streamCodec, handler)) != null) {
            throw new IllegalArgumentException("Duplicated C2S play payload registration: " + type);
        }
    }

    public <T extends CustomPacketPayload> void registerConfiguration(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> streamCodec, ClientConfigurationNetworking.Handler<T> handler) {
        if (this.configurationPayloads.putIfAbsent(type, new PayloadConfigurationRegistration<>(type, streamCodec, handler)) != null) {
            throw new IllegalArgumentException("Duplicated C2S configuration payload registration: " + type);
        }
    }

    private record PayloadPlayRegistration<T extends CustomPacketPayload>(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, ClientPlayNetworking.Handler<T> handler) {
        void register(PayloadRegistrar registrar) {
            registrar.playToClient(type, streamCodec, (payload, context) -> handler.handle(payload, new NeoforgeClientPlayNetworkingContext(context)));
        }
    }

    private record PayloadConfigurationRegistration<T extends CustomPacketPayload>(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> streamCodec, ClientConfigurationNetworking.Handler<T> handler) {
        void register(PayloadRegistrar registrar) {
            registrar.configurationToClient(type, streamCodec, (payload, context) -> handler.handle(payload, new NeoforgeClientConfigurationNetworkingContext(context)));
        }
    }
}
