package de.realleoxian.moonlightcore.api.config.schema;

import de.realleoxian.moonlightcore.api.config.ConfigKey;
import de.realleoxian.moonlightcore.api.config.metadata.ConfigMetadataHolder;
import de.realleoxian.moonlightcore.api.config.metadata.ConfigMetadataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.NonExtendable
public interface ConfigSchema extends ConfigMetadataHolder {
    ConfigSchema getSchema(String key);

    <T> ConfigValue<T> getValue(String key);

    @UnmodifiableView
    Collection<ConfigSchema> getSchemas();

    @UnmodifiableView
    Collection<ConfigValue<?>> getValues();

    @Nullable
    ConfigKey getKey();

    @ApiStatus.NonExtendable
    interface Builder {
        <M, B> Builder metadata(ConfigMetadataType<M, B> type, Consumer<B> func);

        Builder schema(String key);

        <T> ConfigValue<T> define(String key, ConfigValueSerializer<T> serializer, ConfigValueValidator<T> validator, RestartType restartType, Supplier<T> defValue);

        ConfigValue<Integer> defineInt(String key, int minValue, int maxValue, RestartType restartType, Supplier<Integer> defValue);

        ConfigValue<Float> defineFloat(String key, float minValue, float maxValue, RestartType restartType, Supplier<Float> defValue);

        ConfigValue<Boolean> defineBoolean(String key, RestartType restartType, Supplier<Boolean> defValue);

        <E extends Enum<E>> ConfigValue<E> defineEnum(String key, Class<E> enumClass, RestartType restartType, Supplier<E> defValue);

        <T> ListConfigValue<T> defineList(String key, Class<T> elementType, ConfigValueSerializer<T> elementSerializer, ConfigValueValidator<T> elementValidator, RestartType restartType, Supplier<List<T>> defValue);

        ListConfigValue<Integer> defineIntList(String key, int minValue, int maxValue, RestartType restartType, Supplier<List<Integer>> defValue);

        ListConfigValue<Float> defineFloatList(String key, float minValue, float maxValue, RestartType restartType, Supplier<List<Float>> defValue);

        ListConfigValue<Boolean> defineBooleanList(String key, RestartType restartType, Supplier<List<Boolean>> defValue);

        <E extends Enum<E>> ListConfigValue<E> defineEnumList(String key, Class<E> enumClass, RestartType restartType, Supplier<List<E>> defValue);
    }
}
