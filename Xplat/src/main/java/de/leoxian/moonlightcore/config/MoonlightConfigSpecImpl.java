package de.leoxian.moonlightcore.config;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import de.leoxian.moonlightcore.api.config.MoonlightConfigSpec;
import de.leoxian.moonlightcore.api.util.nullness.NotnullSupplier;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MoonlightConfigSpecImpl implements MoonlightConfigSpec {
    private static final Joiner LINE_JOINER = Joiner.on('\n');
    private static final Splitter DOT_SPLITTER = Splitter.on('.');

    public static final IntValueSerializer INT_SERIALIZER = new IntValueSerializer();
    public static final FloatValueSerializer FLOAT_SERIALIZER = new FloatValueSerializer();
    public static final BooleanValueSerializer BOOLEAN_SERIALIZER = new BooleanValueSerializer();

    private final ImmutableMap<String, Object> storage;

    MoonlightConfigSpecImpl(BuilderImpl builder) {
        this.storage = builder.buildStorage();
    }

    public static final class BuilderImpl implements Builder {
        private final LinkedHashMap<String, Object> storage = new LinkedHashMap<>();
        private final List<String> currentPath = new ArrayList<>();
        private final List<String> comment = new ArrayList<>();

        @Override
        public void push(String path) {
            this.currentPath.addAll(split(path));
        }

        @Override
        public void pop(int count) {
            if(count > this.currentPath.size()) {
                throw new IllegalArgumentException("Tried to pop " + count + " from path but there is only " + this.currentPath.size());
            }

            for(int i = 0; i < count; i++) {
                currentPath.removeLast();
            }
        }

        @Override
        public Builder comment(String commentLine) {
            this.comment.add(commentLine);
            return this;
        }


        // -------------------------------------------------------------------------------------------------------------


        @Override
        public <T> ValueKey<T> define(String key, ValueSerializer<T> serializer, Predicate<T> validator, NotnullSupplier<T> defaultValue) {
            ValueKey<T> valueKey = createValueKey(key, validator, serializer, defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }

        @Override
        public <T> ValueKey<T> define(String key, ValueSerializer<T> serializer, NotnullSupplier<T> defaultValue) {
            ValueKey<T> valueKey = createValueKey(key, $ -> true, serializer, defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }


        // -------------------------------------------------------------------------------------------------------------


        @Override
        public ValueKey<Integer> defineInt(String key, Predicate<Integer> validator, NotnullSupplier<Integer> defaultValue) {
            ValueKey<Integer> valueKey = createValueKey(key, validator, INT_SERIALIZER, defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }

        @Override
        public ValueKey<Integer> defineInt(String key, NotnullSupplier<Integer> defaultValue) {
            ValueKey<Integer> valueKey = createValueKey(key, $ -> true, INT_SERIALIZER, defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }


        // -------------------------------------------------------------------------------------------------------------


        @Override
        public ValueKey<Float> defineFloat(String key, Predicate<Float> validator, NotnullSupplier<Float> defaultValue) {
            ValueKey<Float> valueKey = createValueKey(key, validator, FLOAT_SERIALIZER, defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }

        @Override
        public ValueKey<Float> defineFloat(String key, NotnullSupplier<Float> defaultValue) {
            ValueKey<Float> valueKey = createValueKey(key, $ -> true, FLOAT_SERIALIZER, defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }


        // -------------------------------------------------------------------------------------------------------------


        @Override
        public ValueKey<Boolean> defineBoolean(String key, NotnullSupplier<Boolean> defaultValue) {
            ValueKey<Boolean> valueKey = createValueKey(key, $ -> true, BOOLEAN_SERIALIZER, defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }


        // -------------------------------------------------------------------------------------------------------------


        @Override
        public <E extends Enum<E>> ValueKey<E> defineEnum(String key, Class<E> enumClass, Predicate<E> validator, NotnullSupplier<E> defaultValue) {
            ValueKey<E> valueKey = createValueKey(key, validator, new EnumValueSerializer<>(enumClass), defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }

        @Override
        public <E extends Enum<E>> ValueKey<E> defineEnum(String key, Class<E> enumClass, NotnullSupplier<E> defaultValue) {
            ValueKey<E> valueKey = createValueKey(key, $ -> true, new EnumValueSerializer<>(enumClass), defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }


        // -------------------------------------------------------------------------------------------------------------


        @Override
        public <T> ValueKey<List<T>> defineList(String key, Predicate<T> validator, ValueSerializer<T> valueSerializer, NotnullSupplier<List<T>> defaultValue) {
            ValueKey<List<T>> valueKey = createValueKey(key, listValidator(validator), new ListValueSerializer<>(valueSerializer), defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }

        @Override
        public <T> ValueKey<List<T>> defineList(String key, ValueSerializer<T> valueSerializer, NotnullSupplier<List<T>> defaultValue) {
            ValueKey<List<T>> valueKey = createValueKey(key, $ -> true, new ListValueSerializer<>(valueSerializer), defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }


        // -------------------------------------------------------------------------------------------------------------


        @Override
        public ValueKey<List<Integer>> defineIntList(String key, Predicate<Integer> validator, NotnullSupplier<List<Integer>> defaultValue) {
            ValueKey<List<Integer>> valueKey = createValueKey(key, listValidator(validator), new ListValueSerializer<>(INT_SERIALIZER), defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }

        @Override
        public ValueKey<List<Integer>> defineIntList(String key, NotnullSupplier<List<Integer>> defaultValue) {
            ValueKey<List<Integer>> valueKey = createValueKey(key, $ -> true, new ListValueSerializer<>(INT_SERIALIZER), defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }


        // -------------------------------------------------------------------------------------------------------------


        @Override
        public ValueKey<List<Float>> defineFloatList(String key, Predicate<Float> validator, NotnullSupplier<List<Float>> defaultValue) {
            ValueKey<List<Float>> valueKey = createValueKey(key, listValidator(validator), new ListValueSerializer<>(FLOAT_SERIALIZER), defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }

        @Override
        public ValueKey<List<Float>> defineFloatList(String key, NotnullSupplier<List<Float>> defaultValue) {
            ValueKey<List<Float>> valueKey = createValueKey(key, $ -> true, new ListValueSerializer<>(FLOAT_SERIALIZER), defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }


        // -------------------------------------------------------------------------------------------------------------


        @Override
        public ValueKey<List<Boolean>> defineBooleanList(String key, NotnullSupplier<List<Boolean>> defaultValue) {
            ValueKey<List<Boolean>> valueKey = createValueKey(key, $ -> true, new ListValueSerializer<>(BOOLEAN_SERIALIZER), defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }


        // -------------------------------------------------------------------------------------------------------------


        @Override
        public <E extends Enum<E>> ValueKey<List<E>> defineEnumList(String key, Class<E> enumClass, Predicate<E> validator, NotnullSupplier<List<E>> defaultValue) {
            ValueKey<List<E>> valueKey = createValueKey(key, listValidator(validator), new ListValueSerializer<>(new EnumValueSerializer<>(enumClass)), defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }

        @Override
        public <E extends Enum<E>> ValueKey<List<E>> defineEnumList(String key, Class<E> enumClass, NotnullSupplier<List<E>> defaultValue) {
            ValueKey<List<E>> valueKey = createValueKey(key, $ -> true, new ListValueSerializer<>(new EnumValueSerializer<>(enumClass)), defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }


        // -------------------------------------------------------------------------------------------------------------

        @Override
        public ValueKey<Integer> defineRangedInt(String key, int min, int max, NotnullSupplier<Integer> defaultValue) {
            ValueKey<Integer> valueKey = createValueKey(key, val -> val >= min && val <= max, INT_SERIALIZER, defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }

        @Override
        public ValueKey<Float> defineRangedFloat(String key, float min, float max, NotnullSupplier<Float> defaultValue) {
            ValueKey<Float> valueKey = createValueKey(key, val -> val >= min && val <= max, FLOAT_SERIALIZER, defaultValue);
            getOrCreateCategory().put(key, valueKey);
            this.comment.clear();

            return valueKey;
        }


        // -------------------------------------------------------------------------------------------------------------


        private <T> ValueKey<T> createValueKey(String key, Predicate<T> validator, ValueSerializer<T> serializer, NotnullSupplier<T> defaultValue) {
            return new ValueKeyImpl<>(key, ImmutableList.copyOf(this.currentPath), LINE_JOINER.join(this.comment), validator, serializer, defaultValue);
        }

        @SuppressWarnings("unchecked")
        private Map<String, Object> getOrCreateCategory() {
            Map<String, Object> current = this.storage;

            for (String pathPart : this.currentPath) {
                current = (Map<String, Object>) current.computeIfAbsent(pathPart, k -> new LinkedHashMap<>());
            }

            return current;
        }

        private static <T> Predicate<List<T>> listValidator(Predicate<T> elementValidator) {
            return list -> {
                for (T element : list) {
                    if (!elementValidator.test(element)) return false;
                }
                return true;
            };
        }

        private ImmutableMap<String, Object> buildStorage() {
            return ImmutableMap.copyOf(this.storage);
        }
    }

    public static final class ValueKeyImpl<T> implements ValueKey<T> {
        private final String key;
        private final List<String> path;
        private final String description;
        private final ValueSerializer<T> valueSerializer;
        private final Predicate<T> validator;
        private final NotnullSupplier<T> defaultValue;

        private T cachedValue = null;

        ValueKeyImpl(String key, List<String> path, String description, Predicate<T> validator, ValueSerializer<T> valueSerializer, NotnullSupplier<T> defaultValue) {
            this.key = key;
            this.path = path;
            this.description = description;
            this.validator = validator;
            this.valueSerializer = valueSerializer;
            this.defaultValue = defaultValue;
        }

        @Override
        public T get() {
            if(this.cachedValue == null) {
                this.cachedValue = this.defaultValue.get();

                if(this.cachedValue == null) {
                    throw new NullPointerException("Couldn't cache value");
                }
            }

            return this.cachedValue;
        }

        @Override
        public String key() {
            return this.key;
        }

        @Override
        public List<String> path() {
            return this.path;
        }

        @Override
        public String description() {
            return this.description;
        }

        @Override
        public ValueSerializer<T> serializer() {
            return this.valueSerializer;
        }

        @Override
        public Predicate<T> validator() {
            return this.validator;
        }
    }

    public static final class IntValueSerializer implements ValueSerializer<Integer> {
        private IntValueSerializer() {}

        @Override
        public String write(Integer value) {
            return "i(" + value + ")";
        }

        @Override
        public Integer read(String value) {
            try {
                if(value.startsWith("i(")) {
                    if(!value.endsWith(")")) {
                        throw new IllegalArgumentException("Integer values can be parsed with i(<value>) or not having nothing and just the value. You opened the value ('i(') but didn't closed it ')'");
                    }

                    value = value.substring(2, value.length() - 1);
                } else if (value.endsWith(")")) {
                    throw new IllegalArgumentException("Integer values can be parsed with i(<value>) or not having nothing and just the value. You closed the value (')') but didn't opened it 'i('");
                }

                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public static final class FloatValueSerializer implements ValueSerializer<Float> {

        private FloatValueSerializer() {}

        @Override
        public String write(Float value) {
            return "f(" + value + ")";
        }

        @Override
        public Float read(String value) {
            try {
                if(value.startsWith("f(")) {
                    if(!value.endsWith(")")) {
                        throw new IllegalArgumentException("Float values can be parsed with f(<value>) or not having nothing and just the value. You opened the value ('f(') but didn't closed it ')'");
                    }

                    value = value.substring(2, value.length() - 1);
                } else if (value.endsWith(")")) {
                    throw new IllegalArgumentException("Float values can be parsed with f(<value>) or not having nothing and just the value. You closed the value (')') but didn't opened it 'f('");
                }

                return Float.parseFloat(value);
            } catch (NumberFormatException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public static final class BooleanValueSerializer implements ValueSerializer<Boolean> {

        private BooleanValueSerializer() {}

        @Override
        public String write(Boolean value) {
            return "b(" + value + ")";
        }

        @Override
        public Boolean read(String value) {
            if(value.startsWith("b(")) {
                if(!value.endsWith(")")) {
                    throw new IllegalArgumentException("Float values can be parsed with b(<value>) or not having nothing and just the value. You opened the value ('b(') but didn't closed it ')'");
                }

                value = value.substring(2, value.length() - 1);
            } else if (value.endsWith(")")) {
                throw new IllegalArgumentException("Float values can be parsed with b(<value>) or not having nothing and just the value. You closed the value (')') but didn't opened it 'b('");
            }

            return Boolean.parseBoolean(value);
        }
    }

    public static final class EnumValueSerializer<E extends Enum<E>> implements ValueSerializer<E> {
        private final Class<E> enumClass;

        public EnumValueSerializer(Class<E> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public String write(E value) {
            return value.name();
        }

        @Override
        public E read(String value) {
            try {
                value = value.trim().toUpperCase();

                return Enum.valueOf(this.enumClass, value);
            } catch (IllegalArgumentException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public static final class ListValueSerializer<T> implements ValueSerializer<List<T>> {
        private final ValueSerializer<T> valueSerializer;

        public ListValueSerializer(ValueSerializer<T> valueSerializer) {
            this.valueSerializer = valueSerializer;
        }

        @Override
        public String write(List<T> value) {
            return "[" + value.stream().map(this.valueSerializer::write).collect(Collectors.joining(", ")) + "]";
        }

        @Override
        public List<T> read(String value) {
            value = value.trim();
            if(value.startsWith("[")) {
                if(!value.endsWith("]")) {
                    throw new IllegalArgumentException("Lists can be parsed like '[A, B, C, D...]' or just 'A, B, C, D' but there is a '[' but not the closing ']'.");
                }

                value = value.substring(1, value.length() - 1);
            } else if(value.endsWith("]")) {
                throw new IllegalArgumentException("Lists can be parsed like '[A, B, C, D...]' or just 'A, B, C, D' but there is a ']' but not the opening '['.");
            }

            String[] strValues = value.split(",");
            return Arrays.asList(strValues).stream().map(this.valueSerializer::read).toList();
        }
    }

    private static List<String> split(String path) {
        return Lists.newArrayList(DOT_SPLITTER.split(path));
    }
}
