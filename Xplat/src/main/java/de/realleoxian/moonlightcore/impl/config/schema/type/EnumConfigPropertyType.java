package de.realleoxian.moonlightcore.impl.config.schema.type;

import de.realleoxian.moonlightcore.api.config.schema.ConfigPropertyType;
import net.minecraft.network.FriendlyByteBuf;

public class EnumConfigPropertyType<E extends Enum<E>> implements ConfigPropertyType<E> {

    private final Class<E> enumClass;

    public EnumConfigPropertyType(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public String write(E value) {
        return value.toString();
    }

    @Override
    public E read(String str) {
        return Enum.valueOf(this.enumClass, str);
    }

    @Override
    public void encodeToBuf(FriendlyByteBuf byteBuf, E value) {
        byteBuf.writeEnum(value);
    }

    @Override
    public E decodeFromBuf(FriendlyByteBuf byteBuf) {
        return byteBuf.readEnum(this.enumClass);
    }

}
