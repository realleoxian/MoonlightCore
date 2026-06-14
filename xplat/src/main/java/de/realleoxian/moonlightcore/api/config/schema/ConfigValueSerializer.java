package de.realleoxian.moonlightcore.api.config.schema;

import java.util.List;
import java.util.function.Consumer;

public interface ConfigValueSerializer<T> {
    DeserializationResult<T> readFromString(String str);

    String writeToString(T t);

    sealed interface DeserializationResult<T> permits DeserializationResult.Success, DeserializationResult.Error {
        record Success<T>(T value) implements DeserializationResult<T> {
        }

        record Error<T>(String reason) implements DeserializationResult<T> {
        }

        default DeserializationResult<T> ifSuccess(Consumer<T> func) {
            if (isSuccess()) {
                func.accept(((Success<T>) this).value());
            }
            return this;
        }

        default DeserializationResult<T> ifErrors(Consumer<String> func) {
            if (isError()) {
                func.accept(((Error<T>) this).reason());
            }
            return this;
        }

        default Success<T> asSuccess() {
            if (!isSuccess()) {
                throw new IllegalStateException("DeserializationResult isn't success");
            }
            return (Success<T>) this;
        }

        default Error<T> asError() {
            if (!isError()) {
                throw new IllegalStateException("DeserializationResult isn't error");
            }
            return (Error<T>) this;
        }

        default boolean isError() {
            return this instanceof DeserializationResult.Error<T>;
        }

        default boolean isSuccess() {
            return this instanceof DeserializationResult.Success<T>;
        }
    }
}
