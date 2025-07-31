package de.leoxian.moonlightcore.api.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public sealed interface MoonlightCustomPacket<T extends MoonlightCustomPacket<T>> permits MoonlightCustomPacket.ClientBoundCustomPacket, MoonlightCustomPacket.ServerBoundCustomPacket {

    non-sealed interface ServerBoundCustomPacket<T extends ServerBoundCustomPacket<T>> extends MoonlightCustomPacket<T> {}

    non-sealed interface ClientBoundCustomPacket<T extends ClientBoundCustomPacket<T>> extends MoonlightCustomPacket<T> {}

    /**
     * Get the codec of the packet, used when it's being sent and when its being received.
     */
    PacketCodec<T> codec();

    /**
     * Get the packet identifier
     */
    ResourceLocation id();

    /**
     * The representation of a packet codec, used when the packet its being registered
     * @param <T> The type of packet
     */
    interface PacketCodec<T extends MoonlightCustomPacket<T>> {
        /**
         * The encoder method of the packet
         * @param byteBuf The {@link FriendlyByteBuf} used to encode the packet
         * @param msg The packet with the data is being encoded
         */
        void encode(FriendlyByteBuf byteBuf, T msg);

        /**
         * The decoder method of the packet
         * @param byteBuf The {@link FriendlyByteBuf} used to decode the packet
         */
        T decode(FriendlyByteBuf byteBuf);
    }
}
