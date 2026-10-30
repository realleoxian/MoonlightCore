package de.leoxian.moonlightcore.common.config;

import de.leoxian.moonlightcore.common.config.schema.ConfigKey;
import de.leoxian.moonlightcore.common.config.schema.type.*;
import de.leoxian.moonlightcore.common.config.schema.validator.*;
import de.leoxian.moonlightcore.internal.common.config.schema.type.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.function.IntFunction;
import java.util.function.Supplier;

@ApiStatus.NonExtendable
public interface ConfigSchema {
	/// Attempt to retrieve a config's value entry at this schema/category/section.
	/// If this schema's [#key()] its null, it is the root schema.
	/// @param key The key of the config's value entry
	/// @return The config value with the given key, or `null` if isn't at this level or doesn't exist
	@Nullable
	<T> ConfigValue<T> getValue(String key);

	/// Attempt to retrieve a child config schema from this schema level.
	/// @param key The key of the schema/category/section
	/// @return The config schema with the given key, or `null` if isn't at this level or doesn't exist
	@Nullable
	ConfigSchema getSection(String key);

	/// @return An unmodifiable collection of all the config values this schema has
	@UnmodifiableView
	Collection<ConfigValue<?>> getConfigValues();

	/// @return An unmodifiable collection of all the children schemas this schema has
	@UnmodifiableView
	Collection<ConfigSchema> getSchemas();

	/// @return The key of this schema, or `null` if it's root
	@Nullable
	ConfigKey key();

	/// @return The comments of this schema
	Iterable<String> comments();

	/// @return The translation key of this schema
	@Nullable
	String translationKey();

	interface Builder {
		Builder push(String key);

		Builder pop();

		Builder translationKey(String translationKey);

		Builder requiresWorldRestart();

		Builder requiresGameRestart();

		Builder comment(String comment);

		default Builder comment(String... comments) {
			Arrays.stream(comments).forEach(this::comment);
			return this;
		}

		<C extends Collection<Integer>> ConfigValue<C> defineIntCollection(String key, IntFunction<C> factory, int maxSize, int min, int max, C defValue);

		ConfigValue<Integer> defineInt(String key, int min, int max, int defValue);

		default ConfigValue<Integer> defineInt(String key, int defValue) {
			return defineInt(key, Integer.MIN_VALUE, Integer.MAX_VALUE, defValue);
		}

		<C extends Collection<Float>> ConfigValue<C> defineFloatCollection(String key, IntFunction<C> factory, int maxSize, float min, float max, C defValue);

		ConfigValue<Float> defineFloat(String key, float min, float max, float defValue);

		default ConfigValue<Float> defineFloat(String key, float defValue) {
			return defineFloat(key, Float.MIN_VALUE, Float.MAX_VALUE, defValue);
		}

		<C extends Collection<Boolean>> ConfigValue<C> defineBooleanCollection(String key, IntFunction<C> factory, int maxSize, C defValue);

		ConfigValue<Boolean> defineBoolean(String key, boolean defValue);

		<E extends Enum<E>, C extends Collection<E>> ConfigValue<C> defineEnumCollection(String key, IntFunction<C> factory, int maxSize, Class<E> enumType, C defValue);

		<E extends Enum<E>> ConfigValue<E> defineEnum(String key, Class<E> enumType, E defValue);

		<C extends Collection<UUID>> ConfigValue<C> defineUUIDCollection(String key, IntFunction<C> factory, int maxSize, C defValue);

		ConfigValue<UUID> defineUUID(String key, UUID defValue);

		<C extends Collection<Identifier>> ConfigValue<C> defineIdentifierCollection(String key, IntFunction<C> factory, int maxSize, String validNamespace, C defValue);

		ConfigValue<Identifier> defineIdentifier(String key, String validNamespace, Identifier defValue);

		<C extends Collection<Identifier>> ConfigValue<C> defineIdentifierCollection(String key, IntFunction<C> factory, int maxSize, C defValue);

		ConfigValue<Identifier> defineIdentifier(String key, Identifier defValue);
	}
}
