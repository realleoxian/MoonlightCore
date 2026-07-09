package de.leoxian.moonlightcore.common.config.schema.type;

import net.minecraft.network.FriendlyByteBuf;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public record EnumConfigValueType<E extends Enum<E>>(Class<E> enumType) implements ConfigValueType<E> {
    private static final Map<Class<?>, EnumConfigValueType<?>> CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> EnumConfigValueType<E> get(Class<E> enumType) {
        return (EnumConfigValueType<E>) CACHE.computeIfAbsent(enumType, e -> new EnumConfigValueType<E>((Class<E>) e));
    }

    @Override
    public String writeToString(E value) {
        return value.name();
    }

    @Override
    public DeserializationResult<E> readFromString(String string) {
        try {
            string = string.trim();
            return new DeserializationResult.Success<>(Enum.valueOf(enumType, string));
        } catch (Exception e) {
            var errorMessage = """
                    Expected a valid enum value for '%s'.
                        - Found: %s
                        - Valid values: %s
                    """.formatted(enumType.getSimpleName(), string, Arrays.stream(enumType.getEnumConstants()).map(Enum::name).collect(Collectors.joining(", ", "[", "]")));
            return new DeserializationResult.Error<>(List.of(errorMessage));
        }
    }

    @Override
    public void encodeToBuf(FriendlyByteBuf byteBuf, E value) {
        byteBuf.writeEnum(value);
    }

    @Override
    public E decodeFromBuf(FriendlyByteBuf byteBuf) {
        return byteBuf.readEnum(enumType);
    }
}
