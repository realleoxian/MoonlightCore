package de.leoxian.moonlightcore.neoforge.common.network;

import de.leoxian.moonlightcore.common.network.ServerConfigurationNetworking;
import de.leoxian.moonlightcore.common.network.ServerPlayNetworking;
import de.leoxian.moonlightcore.neoforge.common.ModEventBusRegistrable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class NeoforgeServerNetworkHandler implements ModEventBusRegistrable {
    private final Queue<ConfigurationTask> configurationTasks = new ArrayDeque<>();
    private final Map<CustomPacketPayload.Type<?>, PayloadPlayRegistration<?>> playPayloads = new HashMap<>();
    private final Map<CustomPacketPayload.Type<?>, PayloadConfigurationRegistration<?>> configurationPayloads = new HashMap<>();

    @Override
    public void register(IEventBus modEventBus) {
        modEventBus.addListener((RegisterConfigurationTasksEvent event) ->
                configurationTasks.forEach(event::register));

        modEventBus.addListener((RegisterPayloadHandlersEvent event) -> {
            PayloadRegistrar registrar = event.registrar("1");

            if (!this.playPayloads.isEmpty())
                this.playPayloads.values().forEach(r -> r.register(registrar));
            if (!this.configurationPayloads.isEmpty())
                this.configurationPayloads.values().forEach(r -> r.register(registrar));
        });
    }

    public void addTask(ConfigurationTask task) {
        this.configurationTasks.add(task);
    }

    public <T extends CustomPacketPayload> void registerPlayPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, ServerPlayNetworking.Handler<T> handler) {
        if (this.playPayloads.putIfAbsent(type, new PayloadPlayRegistration<>(type, streamCodec, handler)) != null) {
            throw new IllegalArgumentException("Duplicated S2C play payload registration: " + type);
        }
    }

    public <T extends CustomPacketPayload> void registerConfigurationPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> streamCodec, ServerConfigurationNetworking.Handler<T> handler) {
        if (this.configurationPayloads.putIfAbsent(type, new PayloadConfigurationRegistration<>(type, streamCodec, handler)) != null) {
            throw new IllegalArgumentException("Duplicated S2C configuration payload registration: " + type);
        }
    }

    private record PayloadPlayRegistration<T extends CustomPacketPayload>(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, ServerPlayNetworking.Handler<T> handler) {
        public void register(PayloadRegistrar registrar) {
            registrar.playToServer(type, streamCodec, (p, ctx) -> {
                if (ctx.player() instanceof ServerPlayer serverPlayer) {
                    handler.handle(p, new NeoforgeServerPlayContext(ctx, serverPlayer));
                }
            });
        }
    }

    private record PayloadConfigurationRegistration<T extends CustomPacketPayload>(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> streamCodec, ServerConfigurationNetworking.Handler<T> handler) {
        public void register(PayloadRegistrar registrar) {
            registrar.configurationToServer(type, streamCodec, (p, ctx) ->
                    handler.handle(p, new NeoforgeServerConfigurationContext(ctx)));
        }
    }
}
