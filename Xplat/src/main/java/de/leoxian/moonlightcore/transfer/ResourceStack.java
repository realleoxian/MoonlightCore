package de.leoxian.moonlightcore.transfer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ResourceStack<T extends TransferResource<?>>(T resource, int amount) {

    public static <T extends TransferResource<?>> Codec<ResourceStack<T>> codec(Codec<T> codec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                codec.fieldOf("resource").forGetter(ResourceStack::resource),
                Codec.INT.fieldOf("amount").forGetter(ResourceStack::amount)
        ).apply(instance, ResourceStack::new));
    }

    public boolean isEmpty() {
        return StorageUtils.isEmpty(this.resource(), this.amount());
    }


}
