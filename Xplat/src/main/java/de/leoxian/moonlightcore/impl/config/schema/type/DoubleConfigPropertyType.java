package de.leoxian.moonlightcore.impl.config.schema.type;

import de.leoxian.moonlightcore.api.config.schema.ConfigPropertyType;
import net.minecraft.network.FriendlyByteBuf;

public class DoubleConfigPropertyType implements ConfigPropertyType<Double> {
    public static final ConfigPropertyType<Double> INSTANCE = new DoubleConfigPropertyType();

    private DoubleConfigPropertyType() {}

    @Override
    public String write(Double value) {
        return value.toString();
    }

    @Override
    public Double read(String str) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void encodeToBuf(FriendlyByteBuf byteBuf, Double value) {

    }

    @Override
    public Double decodeFromBuf(FriendlyByteBuf byteBuf) {
        return 0.0;
    }
}
