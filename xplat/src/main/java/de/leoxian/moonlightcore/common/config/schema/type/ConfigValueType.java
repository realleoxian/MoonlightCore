package de.leoxian.moonlightcore.common.config.schema.type;

import net.minecraft.network.FriendlyByteBuf;

import java.util.List;
import java.util.function.Consumer;

public interface ConfigValueType<T> {
    String writeToString(T value);

    DeserializationResult<T> readFromString(String string);

    void encodeToBuf(FriendlyByteBuf byteBuf, T value);

    T decodeFromBuf(FriendlyByteBuf byteBuf);

    sealed interface DeserializationResult<T> {
        record Success<T>(T value) implements DeserializationResult<T> {}

        record Error<T>(List<String> errors) implements DeserializationResult<T> {}

        default DeserializationResult<T> ifError(Consumer<List<String>> consumer) {
            if (this instanceof DeserializationResult.Error<T>(List<String> errors)) {
                consumer.accept(errors);
            }
            return this;
        }

        default DeserializationResult<T> ifSuccess(Consumer<T> consumer) {
            if (this instanceof DeserializationResult.Success<T>(T value)) {
                consumer.accept(value);
            }
            return this;
        }
    }
}
