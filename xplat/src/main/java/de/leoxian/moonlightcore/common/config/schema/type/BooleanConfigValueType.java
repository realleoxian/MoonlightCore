package de.leoxian.moonlightcore.common.config.schema.type;

import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public enum BooleanConfigValueType implements ConfigValueType<Boolean> {
    INSTANCE
    ;
    private static final DeserializationResult<Boolean> TRUE = new DeserializationResult.Success<>(true);
    private static final DeserializationResult<Boolean> FALSE = new DeserializationResult.Success<>(false);

    @Override
    public String writeToString(Boolean value) {
        return value.toString();
    }

    @Override
    public DeserializationResult<Boolean> readFromString(String string) {
        if (string.equalsIgnoreCase("true")) {
            return TRUE;
        } else if (string.equalsIgnoreCase("false")) {
            return FALSE;
        }

        return new DeserializationResult.Error<>(List.of("To parse a boolean its expected to be 'true' or 'false'"));
    }

    @Override
    public void encodeToBuf(FriendlyByteBuf byteBuf, Boolean value) {
        byteBuf.writeBoolean(value);
    }

    @Override
    public Boolean decodeFromBuf(FriendlyByteBuf byteBuf) {
        return byteBuf.readBoolean();
    }
}
