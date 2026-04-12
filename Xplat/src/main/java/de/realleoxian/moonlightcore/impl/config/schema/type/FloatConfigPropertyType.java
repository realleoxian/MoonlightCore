package de.realleoxian.moonlightcore.impl.config.schema.type;

import de.realleoxian.moonlightcore.api.config.schema.ConfigPropertyType;
import net.minecraft.network.FriendlyByteBuf;

public class FloatConfigPropertyType implements ConfigPropertyType<Float> {
    public static final ConfigPropertyType<Float> INSTANCE = new FloatConfigPropertyType();

    private FloatConfigPropertyType() {}

    @Override
    public String write(Float value) {
        return value.toString();
    }

    @Override
    public Float read(String str) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
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
