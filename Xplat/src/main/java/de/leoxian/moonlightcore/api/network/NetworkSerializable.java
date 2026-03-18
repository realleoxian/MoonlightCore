package de.leoxian.moonlightcore.api.network;

import net.minecraft.network.FriendlyByteBuf;

public interface NetworkSerializable {

    void writeToBuffer(FriendlyByteBuf byteBuf);

    void readFromBuffer(FriendlyByteBuf byteBuf);

}
