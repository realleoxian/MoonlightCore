package de.realleoxian.moonlightcore.api.network;

import de.realleoxian.moonlightcore.api.MoonlightCore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ServerNetworking {
    static <T extends CustomPacketPayload> void registerConfigurationPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> codec, ConfigurationPayloadHandler<T> handler) {
        MoonlightCore.ABSTRACTION.registerServerConfigurationPayload(type, codec, handler);
    }

    static <T extends CustomPacketPayload> void registerPlayPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PlayPayloadHandler<T> handler) {
        MoonlightCore.ABSTRACTION.registerServerPlayPayload(type, codec, handler);
    }

    static boolean canSendPlayPayload(ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return MoonlightCore.ABSTRACTION.canSendServerPlayPayload(player, type);
    }

    static boolean canSendPlayPayload(ServerPlayer player, CustomPacketPayload payload) {
        return canSendPlayPayload(player, payload.type());
    }

    static boolean canSendConfigurationPayload(ServerConfigurationPacketListenerImpl networkHandler, CustomPacketPayload.Type<?> type) {
        return MoonlightCore.ABSTRACTION.canSendServerConfigurationPayload(networkHandler, type);
    }

    static boolean canSendConfigurationPayload(ServerConfigurationPacketListenerImpl networkHandler, CustomPacketPayload payload) {
        return canSendConfigurationPayload(networkHandler, payload.type());
    }

    @FunctionalInterface
    interface ConfigurationPayloadHandler<T extends CustomPacketPayload> {
        void handle(ServerConfigurationPacketListenerImpl networkListener, MinecraftServer server, PacketSender responseSender, T payload);
    }

    @FunctionalInterface
    interface PlayPayloadHandler<T extends CustomPacketPayload> {
        void handle(ServerGamePacketListenerImpl networkListener, MinecraftServer server, ServerPlayer player, PacketSender responseSender, T payload);
    }
}
