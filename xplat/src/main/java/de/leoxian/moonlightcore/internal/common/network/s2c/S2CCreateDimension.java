package de.leoxian.moonlightcore.internal.common.network.s2c;

import de.leoxian.moonlightcore.client.network.ClientPlayNetworking;
import de.leoxian.moonlightcore.common.util.DynamicRegistryUtils;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;

public record S2CCreateDimension(Identifier id, DimensionType dimensionType) implements CustomPacketPayload {
    public static final Type<S2CCreateDimension> TYPE = new Type<>(Identifier.parse("moonlightcore:create_dimension"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CCreateDimension> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, S2CCreateDimension::id,
            ByteBufCodecs.fromCodec(DimensionType.DIRECT_CODEC), S2CCreateDimension::dimensionType,
            S2CCreateDimension::new
    );

    public static void handle(S2CCreateDimension packet, ClientPlayNetworking.Context context) {
        Identifier id = packet.id();
        ClientPacketListener packetListener = context.packetListener();
        context.enqueueWork(() -> {
            DynamicRegistryUtils.register(packetListener.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE), id, packet::dimensionType);
            packetListener.levels().add(ResourceKey.create(Registries.DIMENSION, id));
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
