package de.realleoxian.moonlightcore.xplat.internal.network.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public record C2SAcceptedModConfigsPacket(Set<ResourceLocation> acceptedModConfigs) implements CustomPacketPayload {
    public static final Type<C2SAcceptedModConfigsPacket> TYPE = new Type<>(ResourceLocation.parse("moonlightcore:accepted_mod_configs"));
    public static final StreamCodec<FriendlyByteBuf, C2SAcceptedModConfigsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(HashSet::new, ResourceLocation.STREAM_CODEC), C2SAcceptedModConfigsPacket::acceptedModConfigs,
            C2SAcceptedModConfigsPacket::new
    );;

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
