package de.leowgc.moonlightcore.api.util;

import net.minecraft.network.FriendlyByteBuf;

public interface NetworkSerializable {

    void writeToBuffer(FriendlyByteBuf byteBuf);

    void readFromBuffer(FriendlyByteBuf byteBuf);

}
