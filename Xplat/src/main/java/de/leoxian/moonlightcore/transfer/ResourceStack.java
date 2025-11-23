package de.leoxian.moonlightcore.transfer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ResourceStack<T>(T resource, int amount) {

    public static <T> Codec<ResourceStack<T>> codec(Codec<T> resourceCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                resourceCodec.fieldOf("resource").forGetter(ResourceStack::resource),
                Codec.INT.fieldOf("amount").forGetter(ResourceStack::amount)
        ).apply(instance, ResourceStack::new));
    }

}
