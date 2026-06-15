package de.realleoxian.moonlightcore.xplat.config.schema.serializer;

import de.realleoxian.moonlightcore.api.config.schema.ConfigValueSerializer;
import net.minecraft.network.FriendlyByteBuf;

public final class EnumConfigValueSerializer<E extends Enum<E>> implements ConfigValueSerializer<E> {
    private final Class<E> enumClass;

    public EnumConfigValueSerializer(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public DeserializationResult<E> readFromString(String str) {
        str = str.trim();
        try {
            final var result = Enum.valueOf(this.enumClass, str);
            return new DeserializationResult.Success<>(result);
        } catch (IllegalArgumentException e) {
            return new DeserializationResult.Error<>("Unable to find enum value '" + str + "'");
        }
    }

    @Override
    public String writeToString(E e) {
        return e.toString();
    }

    @Override
    public void encodeToBuf(FriendlyByteBuf byteBuf, E e) {
        byteBuf.writeEnum(e);
    }

    @Override
    public E decodeFromBuf(FriendlyByteBuf byteBuf) {
        return byteBuf.readEnum(this.enumClass);
    }
}
