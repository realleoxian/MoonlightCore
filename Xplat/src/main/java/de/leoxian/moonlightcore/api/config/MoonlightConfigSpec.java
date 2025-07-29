package de.leoxian.moonlightcore.api.config;

import de.leoxian.moonlightcore.api.util.nullness.NotnullSupplier;

import java.util.List;
import java.util.function.Predicate;

public interface MoonlightConfigSpec {

    void reload(byte[] newData);

    <T> T getRaw(String path);

    interface Builder {
        void push(String path);

        void pop(int count);

        default void pop() {
            this.pop(1);
        }

        Builder comment(String commentLine);


        <T> ValueKey<T> define(String key, ValueSerializer<T> serializer, Predicate<T> validator, NotnullSupplier<T> defaultValue);

        <T> ValueKey<T> define(String key, ValueSerializer<T> serializer, NotnullSupplier<T> defaultValue);

        ValueKey<Integer> defineInt(String key, Predicate<Integer> validator, NotnullSupplier<Integer> defaultValue);

        ValueKey<Integer> defineInt(String key, NotnullSupplier<Integer> defaultValue);

        ValueKey<Float> defineFloat(String key, Predicate<Float> validator, NotnullSupplier<Float> defaultValue);

        ValueKey<Float> defineFloat(String key, NotnullSupplier<Float> defaultValue);

        ValueKey<Boolean> defineBoolean(String key, NotnullSupplier<Boolean> defaultValue);

        <E extends Enum<E>> ValueKey<E> defineEnum(String key, Class<E> enumClass, Predicate<E> validator, NotnullSupplier<E> defaultValue);

        <E extends Enum<E>> ValueKey<E> defineEnum(String key, Class<E> enumClass, NotnullSupplier<E> defaultValue);


        <T> ValueKey<List<T>> defineList(String key, Predicate<T> validator, ValueSerializer<T> valueSerializer, NotnullSupplier<List<T>> defaultValue);

        <T> ValueKey<List<T>> defineList(String key, ValueSerializer<T> valueSerializer, NotnullSupplier<List<T>> defaultValue);

        ValueKey<List<Integer>> defineIntList(String key, Predicate<Integer> validator, NotnullSupplier<List<Integer>> defaultValue);

        ValueKey<List<Integer>> defineIntList(String key, NotnullSupplier<List<Integer>> defaultValue);

        ValueKey<List<Float>> defineFloatList(String key, Predicate<Float> validator, NotnullSupplier<List<Float>> defaultValue);

        ValueKey<List<Float>> defineFloatList(String key, NotnullSupplier<List<Float>> defaultValue);

        ValueKey<List<Boolean>> defineBooleanList(String key, NotnullSupplier<List<Boolean>> defaultValue);


        <E extends Enum<E>> ValueKey<List<E>> defineEnumList(String key, Class<E> enumClass, Predicate<E> validator, NotnullSupplier<List<E>> defaultValue);

        <E extends Enum<E>> ValueKey<List<E>> defineEnumList(String key, Class<E> enumClass, NotnullSupplier<List<E>> defaultValue);


        ValueKey<Integer> defineRangedInt(String key, int min, int max, NotnullSupplier<Integer> defaultValue);

        ValueKey<Float> defineRangedFloat(String key, float min, float max, NotnullSupplier<Float> defaultValue);
    }

    interface ValueKey<T> extends NotnullSupplier<T> {
        String key();

        List<String> path();

        String description();

        ValueSerializer<T> serializer();

        Predicate<T> validator();

        T get();

        default String writeValue() {
            return serializer().write(this.get());
        }
    }

    interface ValueSerializer<T> {
        String write(T value);

        T read(String value);
    }

    enum Side {
        SERVER,
        COMMON,
        CLIENT
    }
}
