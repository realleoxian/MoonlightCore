package de.leoxian.moonlightcore.common.config.schema.type;

import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public enum FloatConfigValueType implements ConfigValueType<Float> {
    INSTANCE
    ;

    @Override
    public String writeToString(Float value) {
        return value.toString();
    }

    @Override
    public DeserializationResult<Float> readFromString(String string) {
        try {
            string = string.trim();
            return new DeserializationResult.Success<>(Float.parseFloat(string));
        } catch (Exception e) {
            return new DeserializationResult.Error<>(List.of(e.getMessage()));
        }
    }

    @Override
    public void encodeToBuf(FriendlyByteBuf byteBuf, Float value) {
        byteBuf.writeFloat(value);
    }

    @Override
    public Float decodeFromBuf(FriendlyByteBuf byteBuf) {
        return byteBuf.readFloat();
    }
}
