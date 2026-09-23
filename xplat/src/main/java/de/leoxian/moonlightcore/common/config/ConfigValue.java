package de.leoxian.moonlightcore.common.config;

import de.leoxian.moonlightcore.common.config.schema.ConfigKey;
import de.leoxian.moonlightcore.common.config.schema.RestartType;
import de.leoxian.moonlightcore.common.config.schema.type.ConfigValueType;
import de.leoxian.moonlightcore.common.config.schema.validator.ConfigValueValidator;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface ConfigValue<T> {
	/// @return The current value associated with this config entry
	T get();

	/// @return The default value of this config
	T defaultValue();

	/// @return The serializer of this config value type
	ConfigValueType<T> type();

	/// @return The validator of this config entry
	ConfigValueValidator<T> validator();

	/// @return the required restart type
	RestartType requiredRestartType();

	/// @return The config key of this entry
	ConfigKey key();

	/// @return The comments of this config entry
	@Nullable
	Iterable<String> comments();

	/// @return The translation key of this config entry
	@Nullable
	String translationKey();
}
