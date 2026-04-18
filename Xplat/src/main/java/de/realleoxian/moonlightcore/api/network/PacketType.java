package de.realleoxian.moonlightcore.api.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record PacketType<MSG>(ResourceLocation name, Class<MSG> type, PacketEncoder<FriendlyByteBuf, MSG> encoder, PacketDecoder<FriendlyByteBuf, MSG> decoder) {

    public FriendlyByteBuf encode(MSG msg) {
        try {
            FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
            encoder.write(msg, byteBuf);

            return byteBuf;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
