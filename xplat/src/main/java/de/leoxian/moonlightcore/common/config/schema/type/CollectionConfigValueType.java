package de.leoxian.moonlightcore.common.config.schema.type;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public record CollectionConfigValueType<E, C extends Collection<E>>(IntFunction<C> factory, int maxSize, ConfigValueType<E> elementType) implements ConfigValueType<C> {
    @Override
    public String writeToString(C value) {
        return value.stream().map(elementType::writeToString).collect(Collectors.joining(",", "[", "]"));
    }

    @Override
    public DeserializationResult<C> readFromString(String string) {
        string = string.trim();
        if (string.startsWith("[")) {
            if (!string.endsWith("]")) {
                return new DeserializationResult.Error<>(List.of("Collections must be parted between '[' and ']', or neither"));
            }
            string = string.substring(1, string.length() - 1);
        } else if (string.endsWith("]")) {
            return new DeserializationResult.Error<>(List.of("Collections must be parted between '[' and ']', or neither"));
        }

        if (string.isEmpty()) {
            return new DeserializationResult.Success<>(factory.apply(0));
        }

        var split = string.split(",");
        if (split.length == 0) {
            return new DeserializationResult.Success<>(factory.apply(0));
        } else if (split.length > maxSize) {
            return new DeserializationResult.Error<>(List.of("Exceeded max collection size: " + split.length + " > " + maxSize));
        }

        C result = factory.apply(split.length);
        var errors = new ArrayList<String>();
        Arrays.stream(split).filter(s -> !s.isEmpty()).map(this.elementType::readFromString).forEach((r) ->
                r.ifError(errors::addAll).ifSuccess(result::add));
        if (!errors.isEmpty()) {
            return new DeserializationResult.Error<>(errors);
        }
        return new DeserializationResult.Success<>(result);
    }

    @Override
    public void encodeToBuf(FriendlyByteBuf byteBuf, C value) {
        byteBuf.writeInt(value.size());
        value.forEach(e -> this.elementType().encodeToBuf(byteBuf, e));
    }

    @Override
    public C decodeFromBuf(FriendlyByteBuf byteBuf) {
        C collection = factory.apply(byteBuf.readInt());
        for (int ignored = 0; ignored < collection.size(); ignored++) {
            collection.add(elementType().decodeFromBuf(byteBuf));
        }
        return collection;
    }
}
