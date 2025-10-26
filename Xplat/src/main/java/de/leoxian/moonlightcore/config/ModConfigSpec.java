package de.leoxian.moonlightcore.config;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.platform.PlatformEnvironment;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModConfigSpec {
    public static final Marker MARKER = MarkerFactory.getMarker("CONFIG");

    private static final Joiner LINE_JOINER = Joiner.on('\n');
    private static final Splitter COLON_SPLITTER = Splitter.on(':');

    public static <T> Pair<ModConfigSpec, T> configure(String modId, String filename, boolean synced, Function<Builder, T> factory) {
        Builder builder = new Builder(modId, filename, synced);
        T t = factory.apply(builder);

        ModConfigSpec spec = new ModConfigSpec(builder);
        ConfigSerializer.readFromFile(spec);

        return Pair.of(spec, t);
    }

    private final Map<String, Category> categoryStorage;
    private final String modId;
    private final String filename;
    private final boolean synced;

    ModConfigSpec(Builder builder) {
        this.categoryStorage = builder.categories.buildOrThrow();
        this.modId = builder.modId;
        this.filename = builder.filename;
        this.synced = builder.synced;

        ConfigManager.registerSpec(this);
    }

    public <T> T getValue(List<String> path, Class<T> typeClass, String key) {
        if(path.isEmpty()) {
            throw new IllegalStateException("Path cannot be empty");
        }

        Category category = this.categoryStorage.get(path.get(0));
        if(category == null) {
            throw new IllegalStateException("Unknown category: " + path.get(0));
        }

        for(int i = 1; i < path.size(); i++) {
            String subName = path.get(i);
            category = category.getSubCategory(subName);

            if(category == null) {
                throw new IllegalStateException("Unknown sub-category: " + subName);
            }
        }

        return category.getValue(typeClass, key);
    }

    public <T> T getValueUnsafe(List<String> path, String key) {
        if(path.isEmpty()) {
            throw new IllegalStateException("Path cannot be empty");
        }

        Category category = this.categoryStorage.get(path.get(0));
        if(category == null) {
            throw new IllegalStateException("Unknown category: " + path.get(0));
        }

        for(int i = 1; i < path.size(); i++) {
            String subName = path.get(i);
            category = category.getSubCategory(subName);

            if(category == null) {
                throw new IllegalStateException("Unknown sub-category: " + subName);
            }
        }

        return category.getValueUnsafe(key);
    }

    public <T> ValueKey<T> getValueKey(List<String> path, String key) {
        if (path.isEmpty()) {
            throw new IllegalStateException("Path cannot be empty");
        }

        Category category = this.categoryStorage.get(path.get(0));
        if (category == null) {
            throw new IllegalStateException("Unknown category: " + path.get(0));
        }

        for (int i = 1; i < path.size(); i++) {
            String subName = path.get(i);
            category = category.getSubCategory(subName);

            if (category == null) {
                throw new IllegalStateException("Unknown sub-category: " + subName);
            }
        }

        return category.getValueUnsafe(key);
    }

    public <T> ValueKey<T> getValueKey(String key, String... path) {
        return this.getValueKey(Arrays.asList(path), key);
    }

    public <T> ValueKey<T> getValueKey(String path, String key) {
        return this.getValueKey(COLON_SPLITTER.splitToList(path), key);
    }

    public Category getCategory(List<String> path) {
        Category category = this.categoryStorage.get(path.get(0));
        if(category == null) {
            throw new IllegalStateException("Unknown category: " + path.get(0));
        }

        for(int i = 1; i < path.size(); i++) {
            category = category.getSubCategory(path.get(i));

            if(category == null) {
                throw new IllegalStateException("Unknown sub-category: " + path.get(i));
            }
        }

        return category;
    }

    public boolean hasCategory(List<String> path) {
        if(path.isEmpty()) {
            return false;
        }

        Category category = this.categoryStorage.get(path.get(0));
        if(category == null) {
            return false;
        }

        for(int i = 1; i < path.size(); i++) {
            String subName = path.get(i);
            category = category.getSubCategory(subName);

            if(category == null) {
                return false;
            }
        }

        return true;
    }

    public boolean hasValue(List<String> path, String key) {
        if (path.isEmpty()) {
            return false;
        }

        Category category = this.categoryStorage.get(path.get(0));
        if (category == null) {
            return false;
        }

        for (int i = 1; i < path.size(); i++) {
            String subName = path.get(i);
            category = category.getSubCategory(subName);

            if (category == null) {
                return false;
            }
        }

        return category.hasValue(key);
    }

    public Category getCategory(String... path) {
        return this.getCategory(Arrays.asList(path));
    }

    public Category getCategory(String path) {
        return this.getCategory(COLON_SPLITTER.splitToList(path));
    }

    public <T> T getValue(Class<T> typeClass, String key, String... path) {
        return this.getValue(Arrays.asList(path), typeClass, key);
    }

    public <T> T getValue(String path, Class<T> typeClass, String key) {
        return this.getValue(COLON_SPLITTER.splitToList(path), typeClass, key);
    }

    public <T> T getValueUnsafe(String key, String... path) {
        return this.getValueUnsafe(Arrays.asList(path), key);
    }

    public <T> T getValueUnsafe(String path, String key) {
        return this.getValueUnsafe(COLON_SPLITTER.splitToList(path), key);
    }


    public boolean hasCategory(String... path) {
        return this.hasCategory(Arrays.asList(path));
    }

    public boolean hasCategory(String path) {
        return this.hasCategory(COLON_SPLITTER.splitToList(path));
    }


    public boolean hasValue(String key, String... path) {
        return this.hasValue(Arrays.asList(path), key);
    }

    public boolean hasValue(String path, String key) {
        return this.hasValue(COLON_SPLITTER.splitToList(path), key);
    }

    public boolean isCategory(List<String> path) {
        return hasCategory(path);
    }

    public boolean isCategory(String... path) {
        return this.hasCategory(Arrays.asList(path));
    }

    public boolean isCategory(String path) {
        return this.hasCategory(COLON_SPLITTER.splitToList(path));
    }

    public boolean isValue(List<String> path, String key) {
        return hasValue(path, key);
    }

    public boolean isValue(String key, String... path) {
        return this.hasValue(Arrays.asList(path), key);
    }

    public boolean isValue(String path, String key) {
        return this.hasValue(COLON_SPLITTER.splitToList(path), key);
    }

    public List<String> categories() {
        return ImmutableList.copyOf(this.categoryStorage.keySet());
    }

    public Path getFilePath() {
        return PlatformEnvironment.get().getConfigDirectory().resolve("%s.ezc".formatted(this.getFullFilename()));
    }

    public String getFilename() {
        return this.filename;
    }

    public String getFullFilename() {
        return this.modId + "." + this.filename;
    }

    public String getModId() {
        return this.modId;
    }

    public boolean isSync() {
        return this.synced;
    }

    public static class Builder {
        private final ImmutableMap.Builder<String, Category> categories = ImmutableMap.builder();
        private final List<String> commentLines = new ArrayList<>();

        private final String modId;
        private final String filename;
        private final boolean synced;

        Builder(String modId, String filename, boolean synced) {
            this.modId = modId;
            this.filename = filename;
            this.synced = synced;
        }

        public Builder comment(String comment) {
            if(comment.isEmpty()) {
                throw new IllegalStateException("Tried to add empty comment line");
            }
            this.commentLines.add(comment);

            return this;
        }

        public Builder comment(String... comment) {
            for(String line : comment) {
                this.comment(line);
            }

            return this;
        }

        public Builder category(String key, Consumer<Category.Builder> builderOutput) {
            Category.Builder builder = new Category.Builder(key, this.buildComment());
            builderOutput.accept(builder);
            this.categories.put(key, new Category(builder));

            return this;
        }

        private String buildComment() {
            String comment = LINE_JOINER.join(this.commentLines);
            this.commentLines.clear();

            return comment;
        }
    }

    public static class Category {
        private final String key;
        private final String comment;
        private final Map<String, ValueKey<?>> keyStorage;
        private final Map<String, Category> subCategoryStorage;

        Category(Builder builder) {
            this.key = builder.key;
            this.comment = builder.comment;
            this.keyStorage = builder.keyStorageBuilder.buildOrThrow();
            this.subCategoryStorage = builder.categoryStorageBuilder.buildOrThrow();
        }

        public <T> T getValue(Class<T> typeClass, String key) {
            if(!this.keyStorage.containsKey(key)) {
                throw new IllegalStateException("Unknown value key");
            }

            Object value = this.keyStorage.get(key).get();
            if(typeClass.isInstance(value)) {
                return typeClass.cast(value);
            }

            throw new IllegalStateException("The key '%s' isn't of type '{%s}'".formatted(key, typeClass.getSimpleName()));
        }

        @SuppressWarnings("unchecked")
        public <T> T getValueUnsafe(String key) {
            if(!this.keyStorage.containsKey(key)) {
                throw new IllegalStateException("Unknown value key");
            }

            return (T) this.keyStorage.get(key).get();
        }

        public ValueKey<?> getValueKey(String key) {
            if(!this.keyStorage.containsKey(key)) {
                throw new IllegalStateException("Unknown key: " + key);
            }

            return this.keyStorage.get(key);
        }

        public Category getSubCategory(String key) {
            if(!this.subCategoryStorage.containsKey(key)) {
                throw new IllegalStateException("Unknown sub-category");
            }

            return this.subCategoryStorage.get(key);
        }

        public boolean hasValue(String key) {
            return this.keyStorage.containsKey(key);
        }

        public boolean hasSubCategory(String key) {
            return this.subCategoryStorage.containsKey(key);
        }

        public List<String> valueKeys() {
            return ImmutableList.copyOf(this.keyStorage.keySet());
        }

        public List<String> subCategories() {
            return ImmutableList.copyOf(this.subCategoryStorage.keySet());
        }

        public String getKey() {
            return this.key;
        }

        public String getComment() {
            return this.comment;
        }

        public static class Builder {
            private final ImmutableMap.Builder<String, ValueKey<?>> keyStorageBuilder = ImmutableMap.builder();
            private final ImmutableMap.Builder<String, Category> categoryStorageBuilder = ImmutableMap.builder();
            private final List<String> commentLines = new ArrayList<>();

            private final String key;
            private final String comment;

            Builder(String key, String comment) {
                this.key = key;
                this.comment = comment;
            }

            public Builder comment(String comment) {
                if(comment.isEmpty()) {
                    throw new IllegalStateException("Tried to add empty comment line");
                }
                this.commentLines.add(comment);

                return this;
            }

            public Builder comment(String... comment) {
                for(String line : comment) {
                    this.comment(line);
                }

                return this;
            }

            public Category subCategory(String key, Consumer<Builder> builderOutput) {
                Builder builder = new Builder(key, this.buildComment());
                builderOutput.accept(builder);

                return new Category(builder);
            }

            // --------------------[GENERIC]--------------------

            public <T> ValueKey<T> define(String key, ValueSerializer<T> serializer, Predicate<T> validator, Supplier<T> defaultValue) {
                ValueKey<T> valueKey = new ValueKey<>(key, this.buildComment(), serializer, validator, defaultValue);
                this.keyStorageBuilder.put(key, valueKey);

                return valueKey;
            }

            public <T> ValueKey<T> define(String key, ValueSerializer<T> serializer, Supplier<T> defaultValue) {
                return this.define(key, serializer, $ -> true, defaultValue);
            }

            public <T> ValueKey<List<T>> defineList(String key, ValueSerializer<T> valueSerializer, Predicate<T> predicate, Supplier<List<T>> defaultValue) {
                return this.define(key, new ListValueKeySerializer<>(valueSerializer), this.listPredicate(predicate), defaultValue);
            }

            public <T> ValueKey<List<T>> defineList(String key, ValueSerializer<T> valueSerializer, Supplier<List<T>> defaultValue) {
                return this.defineList(key, valueSerializer, $ -> true, defaultValue);
            }

            // --------------------[INTEGER]--------------------

            public ValueKey<Integer> defineInt(String key, Predicate<Integer> validator, Supplier<Integer> defaultValue) {
                return this.define(key, new IntValueKeySerializer(), validator, defaultValue);
            }

            public ValueKey<Integer> defineInt(String key, Supplier<Integer> defaultValue) {
                return this.defineInt(key, $ -> true, defaultValue);
            }

            public ValueKey<Integer> defineRangedInt(String key, int min, int max, Supplier<Integer> defaultValue) {
                return this.defineInt(key, (val) -> val >= min && val <= max, defaultValue);
            }

            public ValueKey<List<Integer>> defineIntList(String key, Predicate<Integer> validator, Supplier<List<Integer>> defaultValue) {
                return this.defineList(key, new IntValueKeySerializer(), validator, defaultValue);
            }

            public ValueKey<List<Integer>> defineIntList(String key, Supplier<List<Integer>> defaultValue) {
                return this.defineIntList(key, $ -> true, defaultValue);
            }

            public ValueKey<List<Integer>> defineRangedIntList(String key, int min, int max, Supplier<List<Integer>> defaultValue) {
                return this.defineIntList(key, (val) -> val >= min && val <= max, defaultValue);
            }

            // --------------------[FLOAT]--------------------

            public ValueKey<Float> defineFloat(String key, Predicate<Float> validator, Supplier<Float> defaultValue) {
                return this.define(key, new FloatValueKeySerializer(), validator, defaultValue);
            }

            public ValueKey<Float> defineFloat(String key, Supplier<Float> defaultValue) {
                return this.defineFloat(key, $ -> true, defaultValue);
            }

            public ValueKey<Float> defineRangedFloat(String key, int min, int max, Supplier<Float> defaultValue) {
                return this.defineFloat(key, (val) -> val >= min && val <= max, defaultValue);
            }

            public ValueKey<List<Float>> defineFloatList(String key, Predicate<Float> validator, Supplier<List<Float>> defaultValue) {
                return this.defineList(key, new FloatValueKeySerializer(), validator, defaultValue);
            }

            public ValueKey<List<Float>> defineFloatList(String key, Supplier<List<Float>> defaultValue) {
                return this.defineFloatList(key, $ -> true, defaultValue);
            }

            public ValueKey<List<Float>> defineRangedFloatList(String key, float min, float max, Supplier<List<Float>> defaultValue) {
                return this.defineFloatList(key, (val) -> val >= min && val <= max, defaultValue);
            }

            // --------------------[BOOLEAN]--------------------

            public ValueKey<Boolean> defineBoolean(String key, Supplier<Boolean> defaultValue) {
                return this.define(key, new BooleanValueKeySerializer(), $ -> true, defaultValue);
            }

            public ValueKey<List<Boolean>> defineBooleanList(String key, Supplier<List<Boolean>> defaultValue) {
                return this.defineList(key, new BooleanValueKeySerializer(), $ -> true, defaultValue);
            }

            // --------------------[ENUM]--------------------

            public <E extends Enum<E>> ValueKey<E> defineEnum(String key, Class<E> enumClass, Predicate<E> validator, Supplier<E> defaultValue) {
                return this.define(key, new EnumValueKeySerializer<>(enumClass), validator, defaultValue);
            }

            public <E extends Enum<E>> ValueKey<E> defineEnum(String key, Class<E> enumClass, Supplier<E> defaultValue) {
                return this.define(key, new EnumValueKeySerializer<>(enumClass), $ -> true, defaultValue);
            }

            public <E extends Enum<E>> ValueKey<List<E>> defineEnumList(String key, Class<E> enumClass, Predicate<E> validator, Supplier<List<E>> defaultValue) {
                return this.defineList(key, new EnumValueKeySerializer<>(enumClass), validator, defaultValue);
            }

            public <E extends Enum<E>> ValueKey<List<E>> defineEnumList(String key, Class<E> enumClass, Supplier<List<E>> defaultValue) {
                return this.defineList(key, new EnumValueKeySerializer<>(enumClass), $ -> true, defaultValue);
            }


            private String buildComment() {
                if(this.commentLines.isEmpty()) {
                    return "";
                }

                String comment = LINE_JOINER.join(this.commentLines);
                this.commentLines.clear();

                return comment;
            }

            private <T> Predicate<List<T>> listPredicate(Predicate<T> validator) {
                return (list) -> {
                    for (var entry : list) {
                        if (!validator.test(entry)) {
                            return false;
                        }
                    }

                    return true;
                };
            }
        }
    }

    public static class ValueKey<T> implements Supplier<T> {
        private final String key;
        private final String comment;
        private final ValueSerializer<T> serializer;
        private final Predicate<T> validator;
        private final Supplier<T> defaultValue;

        @Nullable
        private T cachedValue = null;

        ValueKey(String key, String comment, ValueSerializer<T> serializer, Predicate<T> validator, Supplier<T> defaultValue) {
            this.key = key;
            this.comment = comment;
            this.serializer = serializer;
            this.validator = validator;
            this.defaultValue = defaultValue;
        }

        @Override
        public T get() {
            if(this.cachedValue == null) {
                this.cachedValue = this.defaultValue.get();

                if(this.cachedValue == null) {
                    throw new IllegalStateException("Invalid cache");
                }
            }

            return this.cachedValue;
        }

        public String getComment() {
            return this.comment;
        }

        public String getKey() {
            return this.key;
        }

        @ApiStatus.Internal
        String valueToStr() {
            return this.serializer.write(this.get());
        }

        @ApiStatus.Internal
        void cacheValue(String newValue) {
            T val = this.serializer.read(newValue);

            if(!this.validator.test(val)) {
                MoonlightCore.LOGGER.warn(MARKER, "Tried to cache invalid value ({}) on key {}. Returning to default value", newValue, this.cachedValue);
                this.cachedValue = this.defaultValue.get();
                return;
            }

            if(this.cachedValue == null || this.cachedValue != val) {
                MoonlightCore.LOGGER.info(MARKER, "Cached new value on key {}", this.key);
                this.cachedValue = val;
            }
        }
    }

    public static final class IntValueKeySerializer implements ValueSerializer<Integer> {

        private IntValueKeySerializer() {}

        @Override
        public String write(Integer value) {
            return value.toString();
        }

        @Override
        public Integer read(String value) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception){
                throw new RuntimeException(exception);
            }
        }

    }

    public static final class FloatValueKeySerializer implements ValueSerializer<Float> {

        private FloatValueKeySerializer() {}

        @Override
        public String write(Float value) {
            return value.toString();
        }

        @Override
        public Float read(String value) {
            try {
                return Float.parseFloat(value);
            } catch (NumberFormatException exception){
                throw new RuntimeException(exception);
            }
        }

    }

    public static final class BooleanValueKeySerializer implements ValueSerializer<Boolean> {

        private BooleanValueKeySerializer() {}

        @Override
        public String write(Boolean value) {
            return value.toString();
        }

        @Override
        public Boolean read(String value) {
            return Boolean.parseBoolean(value);
        }

    }

    public record EnumValueKeySerializer<E extends Enum<E>>(Class<E> enumClass) implements ValueSerializer<E> {

        @Override
        public String write(E value) {
            return value.toString();
        }

        @Override
        public E read(String value) {
            value = value.trim().toUpperCase();

            try {
                return Enum.valueOf(this.enumClass, value);
            } catch (IllegalArgumentException exception) {
                String validValues = Arrays.stream(this.enumClass.getEnumConstants()).map(Enum::name).collect(Collectors.joining(", "));
                throw new RuntimeException("Invalid enum value: %s.\n- Valid enum values of '%s': %s".formatted(validValues, this.enumClass.getSimpleName(), validValues));
            }
        }

    }

    public record ListValueKeySerializer<T>(ValueSerializer<T> valueSerializer) implements ValueSerializer<List<T>> {

        @Override
        public String write(List<T> value) {
            return "[" + value.stream().map(this.valueSerializer()::write).collect(Collectors.joining(", ")) + "]";
        }

        @Override
        public List<T> read(String value) {
            value = value.trim();
            if(value.startsWith("[")) {
                if(!value.endsWith("]")) {
                    throw new RuntimeException("Lists can be parsed between '[' and ']' or neither. Serializer found a '[' but not a ']'");
                }

                value = value.substring(1, value.length() - 1);
            } else if(value.endsWith("]")) {
                throw new RuntimeException("Lists can be parsed between '[' and ']' or neither. Serializer found a ']' but not a '['");
            }

            return Arrays.stream(value.split(",")).map(this.valueSerializer::read).toList();
        }

    }

    public interface ValueSerializer<T> {

        String write(T value);

        T read(String value);

    }
}
