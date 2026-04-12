package de.realleoxian.moonlightcore.impl.config.schema.type;

import de.realleoxian.moonlightcore.api.config.schema.ConfigPropertyType;
import net.minecraft.network.FriendlyByteBuf;

public class BooleanConfigPropertyType implements ConfigPropertyType<Boolean> {
    public static final ConfigPropertyType<Boolean> INSTANCE = new BooleanConfigPropertyType();

    private BooleanConfigPropertyType() {}

    @Override
    public String write(Boolean value) {
        return value.toString();
    }

    @Override
    public Boolean read(String str) {
        return Boolean.parseBoolean(str.trim());
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
