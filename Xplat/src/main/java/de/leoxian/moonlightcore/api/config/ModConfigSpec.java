package de.leoxian.moonlightcore.api.config;

import de.leoxian.moonlightcore.api.util.nullness.NotnullSupplier;
import de.leoxian.moonlightcore.config.ModConfigSpecImpl;
import de.leoxian.moonlightcore.util.LoaderEnvironment;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ModConfigSpec {
    static <T> T build(Function<Builder, T> factory) {
        return factory.apply(ModConfigSpecImpl.builder());
    }

    Optional<ConfigCategory> getCategory(String categoryId);

    List<String> categoryEntries();

    String modId();

    Side side();

    default Path filePath() {
        return LoaderEnvironment.getConfigPath().resolve(this.modId() + "-" + this.side() + ".ezc");
    }

    interface Builder {
        ConfigCategory pushCategory(String id, @Nullable String description, Consumer<ConfigCategory.Builder> builderOutput);

        ModConfigSpec build(String modId, Side side);
    }

    interface ConfigCategory {
        Optional<ValueKey<?>> getKey(String key);

        <T> Optional<ValueKey<T>> getUncheckedKey(String key);

        Optional<ConfigCategory> getChild(String childKey);

        List<String> childEntries();

        List<String> keyEntries();

        String id();

        String description();

        interface Builder {
            ConfigCategory pushChild(String id, String description, Consumer<Builder> builderOutput);

            Builder description(String... description);

            // ------------------------------------------------------------------------------------------------------------------------

            <T> ValueKey<T> define(String key, ValueKeySerializer<T> serializer, Predicate<T> validator, NotnullSupplier<T> defaultValue);

            <T> ValueKey<T> define(String key, ValueKeySerializer<T> serializer, NotnullSupplier<T> defaultValue);

            <T> ValueKey<List<T>> defineList(String key, ValueKeySerializer<T> serializer, Predicate<T> validator, NotnullSupplier<List<T>> defaultValue);

            <T> ValueKey<List<T>> defineList(String key, ValueKeySerializer<T> serializer, NotnullSupplier<List<T>> defaultValue);

            // ------------------------------------------------------------------------------------------------------------------------

            ValueKey<Integer> defineInt(String key, Predicate<Integer> validator, NotnullSupplier<Integer> defaultValue);

            ValueKey<Integer> defineInt(String key, NotnullSupplier<Integer> defaultValue);

            ValueKey<Integer> defineRangedInt(String key, int minValue, int maxValue, NotnullSupplier<Integer> defaultValue);

            ValueKey<List<Integer>> defineIntList(String key, Predicate<Integer> validator, NotnullSupplier<List<Integer>> defaultValue);

            ValueKey<List<Integer>> defineIntList(String key, NotnullSupplier<List<Integer>> defaultValue);

            ValueKey<List<Integer>> defineRangedIntList(String key, int min, int max, NotnullSupplier<List<Integer>> defaultValue);

            // ------------------------------------------------------------------------------------------------------------------------

            ValueKey<Float> defineFloat(String key, Predicate<Float> validator, NotnullSupplier<Float> defaultValue);

            ValueKey<Float> defineFloat(String key, NotnullSupplier<Float> defaultValue);

            ValueKey<Float> defineRangedFloat(String key, int minValue, int maxValue, NotnullSupplier<Float> defaultValue);

            ValueKey<List<Float>> defineFloatList(String key, Predicate<Float> validator, NotnullSupplier<List<Float>> defaultValue);

            ValueKey<List<Float>> defineFloatList(String key, NotnullSupplier<List<Float>> defaultValue);

            ValueKey<List<Float>> defineRangedFloatList(String key, int min, int max, NotnullSupplier<List<Float>> defaultValue);

            // ------------------------------------------------------------------------------------------------------------------------

            ValueKey<Boolean> defineBoolean(String key, NotnullSupplier<Boolean> defaultValue);

            ValueKey<List<Boolean>> defineBooleanList(String key, NotnullSupplier<List<Boolean>> defaultValue);

            // ------------------------------------------------------------------------------------------------------------------------

            <E extends Enum<E>> ValueKey<E> defineEnum(String key, Class<E> enumClass, NotnullSupplier<E> defaultValue);

            <E extends Enum<E>> ValueKey<List<E>> defineEnumList(String key, Class<E> enumClass, NotnullSupplier<List<E>> defaultValue);
        }
    }

    interface ValueKey<T> extends NotnullSupplier<T> {
        String id();

        String description();

        ValueKeySerializer<T> serializer();

        Predicate<T> validator();

        T get();

        default String writeValue() {
            return this.serializer().write(this.get());
        }
    }

    interface ValueKeySerializer<T> {
        String write(T value);

        T read(String value);
    }

    enum Side {
        SERVER(true),
        COMMON(false),
        CLIENT(false)
        ;

        private final boolean isSynced;

        Side(boolean isSynced) {
            this.isSynced = isSynced;
        }

        public boolean isSynced() {
            return this.isSynced;
        }
    }
}
