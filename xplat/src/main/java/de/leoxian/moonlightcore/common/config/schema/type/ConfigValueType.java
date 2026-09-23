package de.leoxian.moonlightcore.common.config.schema.type;

import net.minecraft.network.FriendlyByteBuf;

import java.util.List;
import java.util.function.Consumer;

public interface ConfigValueType<T> {
	/// Transforms the given value into a string
	/// @param value The value
	/// @return A new string made from the given value
	String writeToString(T value);

	/// Deserializes a string into this config value type's type
	/// @param string The deserialized string
	/// @return [DeserializationResult.Success] or [DeserializationResult.Error] from the given string
	DeserializationResult<T> readFromString(String string);

	/// Encodes the given value into a packet's buffers, used for syncing
	/// @param byteBuf The packet's buffer
	void encodeToBuf(FriendlyByteBuf byteBuf, T value);

	/// Decodes the given value from a packet's buffer, used for syncing
	/// @param byteBuf The packet's buffer
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
