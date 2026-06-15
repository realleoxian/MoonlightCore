package de.realleoxian.moonlightcore.xplat.config.schema.serializer;

import de.realleoxian.moonlightcore.api.config.schema.ConfigValueSerializer;
import net.minecraft.network.FriendlyByteBuf;

public final class FloatConfigValueSerializer implements ConfigValueSerializer<Float> {
    public static final ConfigValueSerializer<Float> INSTANCE = new FloatConfigValueSerializer();

    @Override
    public DeserializationResult<Float> readFromString(String str) {
        str = str.trim();
        try {
            final float result = Float.parseFloat(str);
            return new DeserializationResult.Success<>(result);
        } catch (NumberFormatException e) {
            return new DeserializationResult.Error<>("Unable to parse float: '%s' with errors: \n%s".formatted(str, e.getMessage()));
        }
    }

    @Override
    public String writeToString(Float aFloat) {
        return Float.toString(aFloat);
    }

    @Override
    public void encodeToBuf(FriendlyByteBuf byteBuf, Float aFloat) {
        byteBuf.writeFloat(aFloat);
    }

    @Override
    public Float decodeFromBuf(FriendlyByteBuf byteBuf) {
        return byteBuf.readFloat();
    }

    private FloatConfigValueSerializer() {}
}
