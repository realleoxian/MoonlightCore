package de.leoxian.moonlightcore.api.config.schema;

import net.minecraft.network.FriendlyByteBuf;

public interface ConfigPropertyType<T> {

    String write(T value);

    T read(String str);

    void encodeToBuf(FriendlyByteBuf byteBuf, T value);

    T decodeFromBuf(FriendlyByteBuf byteBuf);

}
