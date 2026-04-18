package de.realleoxian.moonlightcore.api.transfer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.realleoxian.moonlightcore.api.network.PacketDecoder;
import de.realleoxian.moonlightcore.api.network.PacketEncoder;
import net.minecraft.network.FriendlyByteBuf;

public record ResourceStack<T>(T resource, int amount) {

    public static <T> Codec<ResourceStack<T>> codec(Codec<T> resourceCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                resourceCodec.fieldOf("resource").forGetter(ResourceStack::resource),
                Codec.INT.fieldOf("amount").forGetter(ResourceStack::amount)
                ).apply(instance, ResourceStack::new));
    }

    public ResourceStack(FriendlyByteBuf byteBuf, PacketDecoder<FriendlyByteBuf, T> resourceDecoder) {
        this(resourceDecoder.read(byteBuf), byteBuf.readVarInt());
    }

    public void writeToBuffer(FriendlyByteBuf byteBuf, PacketEncoder<FriendlyByteBuf, T> resourceEncoder) {
        resourceEncoder.write(resource(), byteBuf);
        byteBuf.writeVarInt(amount);
    }

}
