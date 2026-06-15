package de.realleoxian.moonlightcore.xplat.internal.network.clientbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public final class S2CRequestAcceptedModConfigsPacket implements CustomPacketPayload {
    public static final S2CRequestAcceptedModConfigsPacket INSTANCE = new S2CRequestAcceptedModConfigsPacket();
    public static final Type<S2CRequestAcceptedModConfigsPacket> TYPE = new Type<>(ResourceLocation.parse("moonlightcore:accepted_mod_configs"));
    public static final StreamCodec<FriendlyByteBuf, S2CRequestAcceptedModConfigsPacket> STREAM_CODEC = StreamCodec.unit(S2CRequestAcceptedModConfigsPacket.INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private S2CRequestAcceptedModConfigsPacket() {}
}
