package de.realleoxian.moonlightcore.api.config.schema;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public record ConfigPropertyType<T>(Function<T, String> toStringFunc, Function<String, T> fromStringFunc, StreamCodec<? super ByteBuf, T> networkCodec) {
    public static final ConfigPropertyType<Byte> BYTE = new ConfigPropertyType<>(Objects::toString, Byte::parseByte, ByteBufCodecs.BYTE);
    public static final ConfigPropertyType<Short> SHORT = new ConfigPropertyType<>(Objects::toString, Short::parseShort, ByteBufCodecs.SHORT);
    public static final ConfigPropertyType<Integer> INT = new ConfigPropertyType<>(Objects::toString, Integer::parseInt, ByteBufCodecs.INT);
    public static final ConfigPropertyType<Float> FLOAT = new ConfigPropertyType<>(Objects::toString, Float::parseFloat, ByteBufCodecs.FLOAT);
    public static final ConfigPropertyType<Double> DOUBLE = new ConfigPropertyType<>(Objects::toString, Double::parseDouble, ByteBufCodecs.DOUBLE);
    public static final ConfigPropertyType<Boolean> BOOLEAN = new ConfigPropertyType<>(Objects::toString, Boolean::parseBoolean, ByteBufCodecs.BOOL);
    public static final ConfigPropertyType<ResourceLocation> RESOURCE_LOCATION = new ConfigPropertyType<>(ResourceLocation::toString, ResourceLocation::parse, ResourceLocation.STREAM_CODEC);

    public static <E extends Enum<E>> ConfigPropertyType<E> enumType(Class<E> enumClass) {
        return new ConfigPropertyType<>(Enum::name, e -> Enum.valueOf(enumClass, e), ByteBufCodecs.VAR_INT.map(i -> enumClass.getEnumConstants()[i], Enum::ordinal));
    }

    public static <T> ConfigPropertyType<List<T>> listType(ConfigPropertyType<T> elementType) {
        return new ConfigPropertyType<>(
                list -> list.stream().map(elementType::toString).collect(java.util.stream.Collectors.joining(",", "[", "]")),
                string -> {
                    String content = string.substring(1, string.length() - 1);
                    if (content.isEmpty()) {
                        return java.util.Collections.emptyList();
                    }
                    String[] elements = content.split(",");
                    return java.util.Arrays.stream(elements).map(String::trim).map(elementType::fromString).collect(java.util.stream.Collectors.toList());
                },
                StreamCodec.of(
                        (buteBuf, list) -> {
                            ByteBufCodecs.VAR_INT.encode(buteBuf, list.size());
                            for (T element : list) {
                                elementType.networkCodec().encode(buteBuf, element);
                            }
                        },
                        (byteBuf) -> {
                            int size = ByteBufCodecs.VAR_INT.decode(byteBuf);
                            java.util.ArrayList<T> list = new java.util.ArrayList<>(size);
                            for (int i = 0; i < size; i++) {
                                list.add(elementType.networkCodec().decode(byteBuf));
                            }
                            return list;
                        })
        );
    }

    public String toString(T t) {
        return toStringFunc().apply(t);
    }

    public T fromString(String string) {
        return fromStringFunc().apply(string);
    }

    public void encodeToBuf(RegistryFriendlyByteBuf byteBuf, T t) {
        networkCodec().encode(byteBuf, t);
    }

    public T decodeFromBuf(RegistryFriendlyByteBuf byteBuf) {
        return networkCodec().decode(byteBuf);
    }
}
