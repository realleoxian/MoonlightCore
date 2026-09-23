package de.leoxian.moonlightcore.common.transfer.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ResourceStack<T>(T resource, int amount) {
	public static <T> Codec<ResourceStack<T>> codec(Codec<T> resourceCodec) {
		return RecordCodecBuilder.create(i -> i.group(
				resourceCodec.fieldOf("resource").forGetter(ResourceStack::resource),
				Codec.INT.fieldOf("amount").forGetter(ResourceStack::amount)
		).apply(i, ResourceStack::new));
	}

	public static <T> StreamCodec<RegistryFriendlyByteBuf, ResourceStack<T>> streamCodec(StreamCodec<? super ByteBuf, T> resourceCodec) {
		return StreamCodec.composite(
				resourceCodec, ResourceStack::resource,
				ByteBufCodecs.INT, ResourceStack::amount,
				ResourceStack::new
		);
	}
}
