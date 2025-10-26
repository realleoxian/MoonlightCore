package de.leoxian.moonlightcore.util;

import io.netty.buffer.ByteBuf;

public interface NetworkSerializable<B extends ByteBuf, S> {

    void encodeToBuf(B buf);

    S decodeFromBuf(B buf);

}
