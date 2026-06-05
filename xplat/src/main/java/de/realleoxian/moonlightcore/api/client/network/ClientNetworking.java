package de.realleoxian.moonlightcore.api.client.network;

import de.realleoxian.moonlightcore.api.client.MoonlightCoreClient;
import de.realleoxian.moonlightcore.api.network.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ClientNetworking {
    static <T extends CustomPacketPayload> void registerConfigurationPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> codec, ConfigurationPayloadHandler<T> handler) {
        MoonlightCoreClient.RUNTIME.registerConfigurationPayload(type, codec, handler);
    }

    static <T extends CustomPacketPayload> void registerPlayPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PlayPayloadHandler<T> handler) {
        MoonlightCoreClient.RUNTIME.registerPlayPayload(type, codec, handler);
    }

    static boolean canSendPlayPayload(CustomPacketPayload.Type<?> type) {
        return MoonlightCoreClient.RUNTIME.canSendPlayPayload(type);
    }

    static boolean canSendConfigurationPayload(CustomPacketPayload.Type<?> type) {
        return MoonlightCoreClient.RUNTIME.canSendConfigurationPayload(type);
    }

    @FunctionalInterface
    interface ConfigurationPayloadHandler<T extends CustomPacketPayload> {
        void handle(ClientConfigurationPacketListenerImpl networkHandler, Minecraft minecraft, PacketSender responseSender, T payload);
    }

    @FunctionalInterface
    interface PlayPayloadHandler<T extends CustomPacketPayload> {
        void handle(Minecraft minecraft, LocalPlayer player, PacketSender responseSender, T payload);
    }
}
