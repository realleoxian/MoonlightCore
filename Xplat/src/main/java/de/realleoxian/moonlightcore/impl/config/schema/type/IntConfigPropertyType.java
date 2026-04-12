package de.realleoxian.moonlightcore.impl.config.schema.type;

import de.realleoxian.moonlightcore.api.config.schema.ConfigPropertyType;
import net.minecraft.network.FriendlyByteBuf;

public class IntConfigPropertyType implements ConfigPropertyType<Integer> {
    public static final ConfigPropertyType<Integer> INSTANCE = new IntConfigPropertyType();

    private IntConfigPropertyType() {}

    @Override
    public String write(Integer value) {
        return value.toString();
    }

    @Override
    public Integer read(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
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
