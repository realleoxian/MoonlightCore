package de.leoxian.moonlightcore.config;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import de.leoxian.moonlightcore.api.config.ModConfigSpec;
import de.leoxian.moonlightcore.api.util.nullness.NotnullSupplier;
import de.leoxian.moonlightcore.util.LoaderEnvironment;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ModConfigSpecImpl implements ModConfigSpec {
    private static final Joiner LINE_JOINER = Joiner.on('\n');

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static final ValueKeySerializer<Integer> INT_VALUE_SERIALIZER = new IntValueKeySerializer();
    public static final ValueKeySerializer<Float> FLOAT_VALUE_SERIALIZER = new FloatValueKeySerializer();
    public static final ValueKeySerializer<Boolean> BOOLEAN_VALUE_SERIALIZER = new BooleanValueKeySerializer();

    private final ImmutableMap<String, ConfigCategory> categories;
    private final String modId;
    private final Side side;

    private ModConfigSpecImpl(Side side, String modId, ImmutableMap<String, ConfigCategory> categories) {
        this.categories = categories;
        this.side = side;
        this.modId = modId;

        Path filePath = LoaderEnvironment.getConfigPath().resolve(this.modId + "-" + this.side + ".ezc");

        ConfigSyncRegistry.tryAdd(this);
        EzcConfigParser.tryReadFromFile(filePath, this);
    }

    @Override
    public Optional<ConfigCategory> getCategory(String categoryId) {
        return Optional.ofNullable(this.categories.get(categoryId));
    }

    @Override
    public List<String> categoryEntries() {
        return ImmutableList.copyOf(this.categories.keySet());
    }

    @Override
    public String modId() {
        return this.modId;
    }

    @Override
    public Side side() {
        return this.side;
    }

    private static final class BuilderImpl implements Builder {
        private final ImmutableMap.Builder<String, ConfigCategory> categories = ImmutableMap.builder();

        @Override
        public ConfigCategory pushCategory(String id, @Nullable String description, Consumer<ConfigCategory.Builder> builderOutput) {
            ConfigCategoryImpl.BuilderImpl builder = new ConfigCategoryImpl.BuilderImpl(id, description);
            builderOutput.accept(builder);

            ConfigCategory category = new ConfigCategoryImpl(builder);
            this.categories.put(category.id(), category);
            return category;
        }

        @Override
        public ModConfigSpec build(String modId, Side side) {
            Preconditions.checkNotNull(modId);
            Preconditions.checkNotNull(side);

            return new ModConfigSpecImpl(side, modId, this.categories.buildOrThrow());
        }
    }

    static final class ConfigCategoryImpl implements ConfigCategory {
        private final ImmutableMap<String, ValueKey<?>> valueKeys;
        private final ImmutableMap<String, ConfigCategory> childCategories;
        private final String id;
        private final String description;

        ConfigCategoryImpl(BuilderImpl builder) {
            this.id = builder.id;
            this.description = builder.description;
            this.valueKeys = builder.valueKeys.buildOrThrow();
            this.childCategories = builder.childCategories.buildOrThrow();
        }

        @Override
        public Optional<ValueKey<?>> getKey(String key) {
            return Optional.ofNullable(this.valueKeys.get(key));
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Optional<ValueKey<T>> getUncheckedKey(String key) {
            return Optional.ofNullable((ValueKey<T>) this.valueKeys.get(key));
        }

        @Override
        public Optional<ConfigCategory> getChild(String childKey) {
            return Optional.ofNullable(this.childCategories.get(childKey));
        }

        @Override
        public List<String> childEntries() {
            return ImmutableList.copyOf(this.childCategories.keySet());
        }

        @Override
        public List<String> keyEntries() {
            return ImmutableList.copyOf(this.valueKeys.keySet());
        }

        @Override
        public String id() {
            return this.id;
        }

        @Override
        public String description() {
            return this.description;
        }

        public static final class BuilderImpl implements ConfigCategory.Builder {
            private final ImmutableMap.Builder<String, ConfigCategory> childCategories = ImmutableMap.builder();
            private final ImmutableMap.Builder<String, ValueKey<?>> valueKeys = ImmutableMap.builder();
            private final List<String> currentDescription = new ArrayList<>();

            private final String id;
            private final String description;

            BuilderImpl(String id, String description) {
                this.id = id;
                this.description = description;
            }

            @Override
            public ConfigCategory pushChild(String id, String description, Consumer<Builder> builderOutput) {
                BuilderImpl builder = new BuilderImpl(id, description);
                builderOutput.accept(builder);

                ConfigCategory category = new ConfigCategoryImpl(builder);
                this.childCategories.put(id, category);

                return category;
            }

            @Override
            public Builder description(String... description) {
                this.currentDescription.addAll(Arrays.asList(description));
                return this;
            }

            @Override
            public <T> ValueKey<T> define(String key, ValueKeySerializer<T> serializer, Predicate<T> validator, NotnullSupplier<T> defaultValue) {
                ValueKey<T> valueKey = new ValueKeyImpl<>(key, this.buildDescription(), serializer, validator, defaultValue);
                this.valueKeys.put(key, valueKey);

                return valueKey;
            }

            @Override
            public <T> ValueKey<T> define(String key, ValueKeySerializer<T> serializer, NotnullSupplier<T> defaultValue) {
                return this.define(key, serializer, $ -> true, defaultValue);
            }

            @Override
            public <T> ValueKey<List<T>> defineList(String key, ValueKeySerializer<T> serializer, Predicate<T> validator, NotnullSupplier<List<T>> defaultValue) {
                return this.define(key, new ListValueKeySerializer<>(serializer), listPredicate(validator), defaultValue);
            }

            @Override
            public <T> ValueKey<List<T>> defineList(String key, ValueKeySerializer<T> serializer, NotnullSupplier<List<T>> defaultValue) {
                return this.defineList(key, serializer, $ -> true, defaultValue);
            }

            @Override
            public ValueKey<Integer> defineInt(String key, Predicate<Integer> validator, NotnullSupplier<Integer> defaultValue) {
                return this.define(key, INT_VALUE_SERIALIZER, validator, defaultValue);
            }

            @Override
            public ValueKey<Integer> defineInt(String key, NotnullSupplier<Integer> defaultValue) {
                return this.defineInt(key, $ -> true, defaultValue);
            }

            @Override
            public ValueKey<Integer> defineRangedInt(String key, int minValue, int maxValue, NotnullSupplier<Integer> defaultValue) {
                return this.defineInt(key, (val) -> val >= minValue && val <= maxValue, defaultValue);
            }

            @Override
            public ValueKey<List<Integer>> defineIntList(String key, Predicate<Integer> validator, NotnullSupplier<List<Integer>> defaultValue) {
                return this.defineList(key, INT_VALUE_SERIALIZER, validator, defaultValue);
            }

            @Override
            public ValueKey<List<Integer>> defineIntList(String key, NotnullSupplier<List<Integer>> defaultValue) {
                return this.defineIntList(key, $ -> true, defaultValue);
            }

            @Override
            public ValueKey<List<Integer>> defineRangedIntList(String key, int min, int max, NotnullSupplier<List<Integer>> defaultValue) {
                return this.defineList(key, INT_VALUE_SERIALIZER, (val) -> val >= min && val <= max, defaultValue);
            }

            @Override
            public ValueKey<Float> defineFloat(String key, Predicate<Float> validator, NotnullSupplier<Float> defaultValue) {
                return this.define(key, FLOAT_VALUE_SERIALIZER, validator, defaultValue);
            }

            @Override
            public ValueKey<Float> defineFloat(String key, NotnullSupplier<Float> defaultValue) {
                return this.defineFloat(key, $ -> true, defaultValue);
            }

            @Override
            public ValueKey<Float> defineRangedFloat(String key, int minValue, int maxValue, NotnullSupplier<Float> defaultValue) {
                return this.defineFloat(key, (val) -> val >= minValue && val <= maxValue, defaultValue);
            }

            @Override
            public ValueKey<List<Float>> defineFloatList(String key, Predicate<Float> validator, NotnullSupplier<List<Float>> defaultValue) {
                return this.defineList(key, FLOAT_VALUE_SERIALIZER, validator, defaultValue);
            }

            @Override
            public ValueKey<List<Float>> defineFloatList(String key, NotnullSupplier<List<Float>> defaultValue) {
                return this.defineFloatList(key, $ -> true, defaultValue);
            }

            @Override
            public ValueKey<List<Float>> defineRangedFloatList(String key, int min, int max, NotnullSupplier<List<Float>> defaultValue) {
                return this.defineList(key, FLOAT_VALUE_SERIALIZER, (val) -> val >= min && val <= max, defaultValue);
            }

            @Override
            public ValueKey<Boolean> defineBoolean(String key, NotnullSupplier<Boolean> defaultValue) {
                return this.define(key, BOOLEAN_VALUE_SERIALIZER, $ -> true, defaultValue);
            }

            @Override
            public ValueKey<List<Boolean>> defineBooleanList(String key, NotnullSupplier<List<Boolean>> defaultValue) {
                return this.defineList(key, BOOLEAN_VALUE_SERIALIZER, $ -> true, defaultValue);
            }

            @Override
            public <E extends Enum<E>> ValueKey<E> defineEnum(String key, Class<E> enumClass, NotnullSupplier<E> defaultValue) {
                return this.define(key, new EnumValueKeySerializer<>(enumClass), $ -> true, defaultValue);
            }

            @Override
            public <E extends Enum<E>> ValueKey<List<E>> defineEnumList(String key, Class<E> enumClass, NotnullSupplier<List<E>> defaultValue) {
                return this.defineList(key, new EnumValueKeySerializer<>(enumClass), $ -> true, defaultValue);
            }

            private String buildDescription() {
                String description = LINE_JOINER.join(this.currentDescription);
                this.currentDescription.clear();

                return description;
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

    static final class ValueKeyImpl<T> implements ValueKey<T> {
        private final String id;
        private final String description;
        private final ValueKeySerializer<T> serializer;
        private final Predicate<T> validator;
        private final NotnullSupplier<T> defaultValue;

        private T cachedValue = null;

        ValueKeyImpl(String id, String description, ValueKeySerializer<T> serializer, Predicate<T> validator, NotnullSupplier<T> defaultValue) {
            this.id = id;
            this.description = description;
            this.serializer = serializer;
            this.validator = validator;
            this.defaultValue = defaultValue;
        }

        public void cacheValue(String strValue) {
            T value = this.serializer.read(strValue);

            if(!this.validator.test(value)) {
                throw new RuntimeException("Invalid value " + value + " for key " + this.id());
            } else {
                if(value != this.get()) {
                    LogUtils.getLogger().debug("Caching value: {} for key {}", value, this.id);
                    this.cachedValue = value;
                }
            }
        }

        @Override
        public T get() {
            if(this.cachedValue == null) {
                this.cachedValue = this.defaultValue.get();

                if(this.cachedValue == null) {
                    throw new RuntimeException("Invalid cache");
                }
            }

            return this.cachedValue;
        }

        @Override
        public String id() {
            return this.id;
        }

        @Override
        public String description() {
            return this.description;
        }

        @Override
        public ValueKeySerializer<T> serializer() {
            return this.serializer;
        }

        @Override
        public Predicate<T> validator() {
            return this.validator;
        }
    }

    public static final class IntValueKeySerializer implements ValueKeySerializer<Integer> {
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

    public static final class FloatValueKeySerializer implements ValueKeySerializer<Float> {
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

    public static final class BooleanValueKeySerializer implements ValueKeySerializer<Boolean> {
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

    public record EnumValueKeySerializer<E extends Enum<E>>(Class<E> enumClass) implements ValueKeySerializer<E> {
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

    public record ListValueKeySerializer<T>(ValueKeySerializer<T> valueSerializer) implements ValueKeySerializer<List<T>> {
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

}
