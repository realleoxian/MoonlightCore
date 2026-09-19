package de.leoxian.moonlightcore.common.config;

import de.leoxian.moonlightcore.common.config.schema.ConfigKey;
import de.leoxian.moonlightcore.common.config.schema.type.*;
import de.leoxian.moonlightcore.common.config.schema.validator.*;
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

        <T> ConfigValue<T> define(String key, ConfigValueType<T> type, ConfigValueValidator<T> validator, Supplier<T> defValue);

        default ConfigValue<Integer> defineInt(String key, int min, int max, Supplier<Integer> defValue) {
            return define(key, IntConfigValueType.INSTANCE, new RangedConfigValidator<>(min, max), defValue);
        }

        default ConfigValue<Integer> defineInt(String key, Supplier<Integer> defValue) {
            return defineInt(key, Integer.MIN_VALUE, Integer.MAX_VALUE, defValue);
        }

        default ConfigValue<Float> defineFloat(String key, float min, float max, Supplier<Float> defValue) {
            return define(key, FloatConfigValueType.INSTANCE, new RangedConfigValidator<>(min, max), defValue);
        }

        default ConfigValue<Float> defineFloat(String key, Supplier<Float> defValue) {
            return defineFloat(key, Float.MIN_VALUE, Float.MAX_VALUE, defValue);
        }

        default ConfigValue<Boolean> defineBool(String key, Supplier<Boolean> defValue) {
            return define(key, BooleanConfigValueType.INSTANCE, NoOpConfigValidator.INSTANCE.cast(), defValue);
        }

        default <E extends Enum<E>> ConfigValue<E> defineEnum(String key, Class<E> enumType, Supplier<E> defValue) {
            return define(key, EnumConfigValueType.get(enumType), NoOpConfigValidator.INSTANCE.cast(), defValue);
        }

        default ConfigValue<UUID> defineUUID(String key, Supplier<UUID> defValue) {
            return define(key, UUIDConfigValueType.INSTANCE, NoOpConfigValidator.INSTANCE.cast(), defValue);
        }

        default ConfigValue<Identifier> defineIdentifier(String key, String validNamespace, Supplier<Identifier> defValue) {
            return define(key, IdentifierConfigValueType.INSTANCE, new IdentifierConfigValidator(validNamespace), defValue);
        }

        default ConfigValue<Identifier> defineIdentifier(String key, Supplier<Identifier> defValue) {
            return define(key, IdentifierConfigValueType.INSTANCE, IdentifierConfigValidator.ANY, defValue);
        }

        default <E, C extends Collection<E>> ConfigValue<C> defineCollection(String key, IntFunction<C> factory, int maxSize, ConfigValueType<E> elementType, ConfigValueValidator<E> elementValidator, Supplier<C> defValue) {
            return define(key, new CollectionConfigValueType<E, C>(factory, maxSize, elementType), new CollectionConfigValidator<E, C>(elementValidator), defValue);
        }
    }
}
