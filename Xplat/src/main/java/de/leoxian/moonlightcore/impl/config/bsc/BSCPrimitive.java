package de.leoxian.moonlightcore.impl.config.bsc;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface BSCPrimitive<T> {

    T value();

    ResourceLocation typeId();

    interface Type<T, B extends BSCPrimitive<T>> {

        void encodeToBuf(ByteBuf byteBuf, B primitive);

        B decodeFromBuf(ByteBuf byteBuf);

        String writeToString(B primitive);

        B readFromString(String str);

        String getTypeName();

        int getByteSize();

        ResourceLocation getId();

    }

}
