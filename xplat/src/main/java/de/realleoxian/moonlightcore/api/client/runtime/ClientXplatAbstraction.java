package de.realleoxian.moonlightcore.api.client.runtime;

import de.realleoxian.moonlightcore.api.ModLoadContext;
import de.realleoxian.moonlightcore.api.client.ClientModContainer;
import de.realleoxian.moonlightcore.api.client.network.ClientNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.Consumer;

public interface ClientXplatAbstraction<T extends ModLoadContext> {
    void initializeClientMod(String modId, T loadContext, Consumer<ClientModContainer> initializer);

    // -----[CLIENT NETWORKING]-----

    <MSG extends CustomPacketPayload> void registerConfigurationPayload(CustomPacketPayload.Type<MSG> type, StreamCodec<? super FriendlyByteBuf, MSG> codec, ClientNetworking.ConfigurationPayloadHandler<MSG> handler);

    <MSG extends CustomPacketPayload> void registerPlayPayload(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> codec, ClientNetworking.PlayPayloadHandler<MSG> handler);

    boolean canSendPlayPayload(CustomPacketPayload.Type<?> type);

    boolean canSendConfigurationPayload(CustomPacketPayload.Type<?> type);
}
