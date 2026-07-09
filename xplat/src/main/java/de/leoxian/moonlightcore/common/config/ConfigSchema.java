package de.leoxian.moonlightcore.common.config;

import de.leoxian.moonlightcore.common.config.schema.ConfigKey;
import de.leoxian.moonlightcore.common.config.schema.type.*;
import de.leoxian.moonlightcore.common.config.schema.validator.*;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.function.IntFunction;
import java.util.function.Supplier;

@ApiStatus.NonExtendable
public interface ConfigSchema {
    @Nullable
    <T> ConfigValue<T> getValue(String key);

    @Nullable
    ConfigSchema getSection(String key);

    Collection<ConfigValue<?>> getConfigValues();

    Collection<ConfigSchema> getSchemas();

    @Nullable
    ConfigKey key();

    Iterable<String> comments();

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
