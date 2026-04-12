package de.realleoxian.moonlightcore.api.config.schema;

import de.realleoxian.moonlightcore.api.config.internal.LoadedConfig;
import de.realleoxian.moonlightcore.impl.config.schema.type.*;
import de.realleoxian.moonlightcore.impl.config.schema.validator.ListConfigValidator;
import de.realleoxian.moonlightcore.impl.config.schema.validator.NoOpConfigValidator;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface ConfigSchema {

    void accept(LoadedConfig config);

    void doSync(ServerPlayer player);

    Collection<ConfigProperty<?>> getRootProperties();

    Collection<ConfigSection> getSections();

    interface Builder {

        Builder push(String path);

        Builder pop(int count);

        default Builder pop() {
            return pop(1);
        }

        Builder comment(String comment);

        default Builder comment(String... comments) {
            Arrays.stream(comments).forEach(this::comment);
            return this;
        }

        Builder translationKey(String key);

        Builder worldRestart();

        Builder gameRestart();

        <T> ConfigProperty<T> define(String key, ConfigPropertyType<T> type, ConfigPropertyValidator<T> validator, Supplier<T> defValue);

        @SuppressWarnings("unchecked")
        default <T extends Comparable<T>> ConfigProperty<T> define(String key, ConfigPropertyType<T> type, Supplier<T> defValue) {
            return define(key, type, (ConfigPropertyValidator<T>) NoOpConfigValidator.INSTANCE, defValue);
        }

        default <T extends Comparable<T>> ConfigProperty<T> defineRanged(String key, ConfigPropertyType<T> type, Class<T> clazz, T min, T max, Supplier<T> defValue) {
            return define(key, type, ConfigPropertyValidator.ranged(clazz, min, max), defValue);
        }

        default <E> ConfigProperty<List<E>> defineList(String key, ConfigPropertyType<E> elementType, int maxSize, ConfigPropertyValidator<E> elementValidator, Supplier<List<E>> defValue) {
            return define(key, new ListConfigPropertyType<>(elementType, maxSize), new ListConfigValidator<>(elementValidator), defValue);
        }

        default <E extends Comparable<E>> ConfigProperty<List<E>> defineRangedElementList(String key, ConfigPropertyType<E> elementType, int maxSize, Class<E> clazz, E min, E max, Supplier<List<E>> defValue) {
            return defineList(key, elementType, maxSize, ConfigPropertyValidator.ranged(clazz, min, max), defValue);
        }

        default ConfigProperty<Integer> defineInt(String key, int min, int max, Supplier<Integer> defValue) {
            return define(key, IntConfigPropertyType.INSTANCE, ConfigPropertyValidator.ranged(Integer.class, min, max), defValue);
        }

        default ConfigProperty<Integer> defineInt(String key, Supplier<Integer> defValue) {
            return defineInt(key, Integer.MIN_VALUE, Integer.MAX_VALUE, defValue);
        }

        default ConfigProperty<List<Integer>> defineIntList(String key, int maxSize, int min, int max, Supplier<List<Integer>> defValue) {
            return defineRangedElementList(key, IntConfigPropertyType.INSTANCE, maxSize, Integer.class, min, max, defValue);
        }

        default ConfigProperty<List<Integer>> defineIntList(String key, int maxSize, Supplier<List<Integer>> defValue) {
            return defineIntList(key, maxSize, Integer.MIN_VALUE, Integer.MAX_VALUE, defValue);
        }

        default ConfigProperty<Float> defineFloat(String key, float min, float max, Supplier<Float> defValue) {
            return define(key, FloatConfigPropertyType.INSTANCE, ConfigPropertyValidator.ranged(Float.class, min, max), defValue);
        }

        default ConfigProperty<Float> defineFloat(String key, Supplier<Float> defValue) {
            return defineFloat(key, Float.MIN_VALUE, Float.MAX_VALUE, defValue);
        }

        default ConfigProperty<List<Float>> defineFloatList(String key, int maxSize, float min, float max, Supplier<List<Float>> defValue) {
            return defineRangedElementList(key, FloatConfigPropertyType.INSTANCE, maxSize, Float.class, min, max, defValue);
        }

        default ConfigProperty<List<Float>> defineFloatList(String key, int maxSize, Supplier<List<Float>> defValue) {
            return defineFloatList(key, maxSize, Float.MIN_VALUE, Float.MAX_VALUE, defValue);
        }

        default ConfigProperty<Double> defineDouble(String key, double min, double max, Supplier<Double> defValue) {
            return define(key, DoubleConfigPropertyType.INSTANCE, ConfigPropertyValidator.ranged(Double.class, min, max), defValue);
        }

        default ConfigProperty<Double> defineDouble(String key, Supplier<Double> defValue) {
            return defineDouble(key, Double.MIN_VALUE, Double.MAX_VALUE, defValue);
        }

        default ConfigProperty<List<Double>> defineDoubleList(String key, int maxSize, double min, double max, Supplier<List<Double>> defValue) {
            return defineRangedElementList(key, DoubleConfigPropertyType.INSTANCE, maxSize, Double.class, min, max, defValue);
        }

        default ConfigProperty<List<Double>> defineDoubleList(String key, int maxSize, Supplier<List<Double>> defValue) {
            return defineDoubleList(key, maxSize, Double.MIN_VALUE, Double.MAX_VALUE, defValue);
        }

        @SuppressWarnings("unchecked")
        default ConfigProperty<Boolean> defineBoolean(String key, Supplier<Boolean> defValue) {
            return define(key, BooleanConfigPropertyType.INSTANCE, (ConfigPropertyValidator<Boolean>) NoOpConfigValidator.INSTANCE, defValue);
        }

        @SuppressWarnings("unchecked")
        default ConfigProperty<List<Boolean>> defineBooleanList(String key, int maxSize, Supplier<List<Boolean>> defValue) {
            return defineList(key, BooleanConfigPropertyType.INSTANCE, maxSize, (ConfigPropertyValidator<Boolean>) NoOpConfigValidator.INSTANCE, defValue);
        }

        @SuppressWarnings("unchecked")
        default <E extends Enum<E>> ConfigProperty<E> defineEnum(String key, Class<E> enumClass, Supplier<E> defValue) {
            return define(key, new EnumConfigPropertyType<>(enumClass), (ConfigPropertyValidator<E>) NoOpConfigValidator.INSTANCE, defValue);
        }

        @SuppressWarnings("unchecked")
        default <E extends Enum<E>> ConfigProperty<List<E>> defineEnumList(String key, Class<E> enumClass, int maxSize, Supplier<List<E>> defValue) {
            return defineList(key, new EnumConfigPropertyType<>(enumClass), maxSize, (ConfigPropertyValidator<E>) NoOpConfigValidator.INSTANCE, defValue);
        }

        void build();

    }

}
