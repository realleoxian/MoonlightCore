package de.leoxian.moonlightcore.common.config.schema.type;

import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public enum IntConfigValueType implements ConfigValueType<Integer> {
    INSTANCE
    ;

    @Override
    public String writeToString(Integer value) {
        return value.toString();
    }

    @Override
    public DeserializationResult<Integer> readFromString(String string) {
        try {
            string = string.trim();
            return new DeserializationResult.Success<>(Integer.parseInt(string));
        } catch (NumberFormatException e) {
            return new DeserializationResult.Error<>(List.of(e.getMessage()));
        }
    }

    @Override
    public void encodeToBuf(FriendlyByteBuf byteBuf, Integer value) {
        byteBuf.writeInt(value);
    }

    @Override
    public Integer decodeFromBuf(FriendlyByteBuf byteBuf) {
        return byteBuf.readInt();
    }
}
