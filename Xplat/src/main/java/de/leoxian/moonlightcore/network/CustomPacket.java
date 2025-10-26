package de.leoxian.moonlightcore.network;

import de.leoxian.moonlightcore.util.StreamCodec;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface CustomPacket<S extends CustomPacket<S>> {

     StreamCodec<FriendlyByteBuf, S> codec();

     ResourceLocation id();

}