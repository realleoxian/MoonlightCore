package de.realleoxian.moonlightcore.api.network;

import net.minecraft.network.FriendlyByteBuf;

public interface NetworkSerializable {

    void writeToBuffer(FriendlyByteBuf byteBuf);

    void readFromBuffer(FriendlyByteBuf byteBuf);

}
