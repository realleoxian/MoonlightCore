package de.leoxian.moonlightcore.internal.common.network.s2c;

import de.leoxian.moonlightcore.client.network.ClientPlayNetworking;
import de.leoxian.moonlightcore.common.util.DynamicRegistryUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public record S2CRemoveDimensionPacket(Identifier identifier) implements CustomPacketPayload {
    public static final Type<S2CRemoveDimensionPacket> TYPE = new Type<>(Identifier.parse("moonlightcore:remove_dimension"));
    public static final StreamCodec<ByteBuf, S2CRemoveDimensionPacket> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, S2CRemoveDimensionPacket::identifier,
            S2CRemoveDimensionPacket::new
    );

    public static void handle(S2CRemoveDimensionPacket packet, ClientPlayNetworking.Context context) {
        Identifier id = packet.identifier();
        ClientPacketListener packetListener = context.packetListener();
        context.enqueueWork(() -> {
            DynamicRegistryUtils.unregister(packetListener.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE), id);
            packetListener.levels().remove(ResourceKey.create(Registries.DIMENSION, id));
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
