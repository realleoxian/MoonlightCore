package de.realleoxian.moonlightcore.api.config.schema;

import de.realleoxian.moonlightcore.api.config.metadata.ConfigMetadataHolder;
import de.realleoxian.moonlightcore.api.config.schema.validator.ConfigPropertyValidator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ConfigSchema extends ConfigMetadataHolder {
    ConfigProperty<?> getProperty(String key);

    ConfigSchema getSchema(String key);

    @UnmodifiableView
    Collection<ConfigProperty<?>> properties();

    @UnmodifiableView
    Collection<ConfigSchema> schemas();

    ConfigKey key();

    interface Builder extends ConfigMetadataHolder.Builder<ConfigSchema.Builder> {
        Builder schema(String key, Consumer<ConfigSchema.Builder> builderModifier);

        <T> ConfigProperty<T> property(String key, ConfigPropertyType<T> type, Supplier<T> defaultValue, Consumer<ConfigProperty.Builder<T>> builderModifier);

        default ConfigProperty<Byte> byteProperty(String key, Supplier<Byte> defaultValue, Consumer<ConfigProperty.Builder<Byte>> builderModifier) {
            return property(key, ConfigPropertyType.BYTE, defaultValue, builderModifier);
        }

        default ConfigProperty<Short> shortProperty(String key, Supplier<Short> defaultValue, Consumer<ConfigProperty.Builder<Short>> builderModifier) {
            return property(key, ConfigPropertyType.SHORT, defaultValue, builderModifier);
        }

        default ConfigProperty<Integer> intProperty(String key, Supplier<Integer> defaultValue, Consumer<ConfigProperty.Builder<Integer>> builderModifier) {
            return property(key, ConfigPropertyType.INT, defaultValue, builderModifier);
        }

        default ConfigProperty<Float> floatProperty(String key, Supplier<Float> defaultValue, Consumer<ConfigProperty.Builder<Float>> builderModifier) {
            return property(key, ConfigPropertyType.FLOAT, defaultValue, builderModifier);
        }

        default ConfigProperty<Double> doubleProperty(String key, Supplier<Double> defaultValue, Consumer<ConfigProperty.Builder<Double>> builderModifier) {
            return property(key, ConfigPropertyType.DOUBLE, defaultValue, builderModifier);
        }

        default ConfigProperty<Boolean> boolProperty(String key, Supplier<Boolean> defaultValue, Consumer<ConfigProperty.Builder<Boolean>> builderModifier) {
            return property(key, ConfigPropertyType.BOOLEAN, defaultValue, builderModifier);
        }

        default <E extends Enum<E>> ConfigProperty<E> enumProperty(String key, Class<E> enumType, Supplier<E> defaultValue, Consumer<ConfigProperty.Builder<E>> builderModifier){
            return property(key, ConfigPropertyType.enumType(enumType), defaultValue, builderModifier);
        }

        default ConfigProperty<ResourceLocation> resourceLocationProperty(String key, Supplier<ResourceLocation> defaultValue, Consumer<ConfigProperty.Builder<ResourceLocation>> builderModifier){
            return property(key, ConfigPropertyType.RESOURCE_LOCATION, defaultValue, builderModifier);
        }

        <T> ListConfigProperty<T> listProperty(String key, ConfigPropertyType<T> elementType, ConfigPropertyValidator<T> elementValidator, Supplier<List<T>> defaultValue, Consumer<ConfigProperty.Builder<List<T>>> builderModifier);

        default ListConfigProperty<Byte> byteListProperty(String key, ConfigPropertyValidator<Byte> elementValidator, Supplier<List<Byte>> defaultValue, Consumer<ConfigProperty.Builder<List<Byte>>> builderModifier) {
            return listProperty(key, ConfigPropertyType.BYTE, elementValidator, defaultValue, builderModifier);
        }

        @SuppressWarnings("unchecked")
        default ListConfigProperty<Byte> byteListProperty(String key, Supplier<List<Byte>> defaultValue, Consumer<ConfigProperty.Builder<List<Byte>>> builderModifier) {
            return byteListProperty(key, (ConfigPropertyValidator<Byte>) ConfigPropertyValidator.NO_OP, defaultValue, builderModifier);
        }

        default ListConfigProperty<Short> shortListProperty(String key, ConfigPropertyValidator<Short> elementValidator, Supplier<List<Short>> defaultValue, Consumer<ConfigProperty.Builder<List<Short>>> builderModifier) {
            return listProperty(key, ConfigPropertyType.SHORT, elementValidator, defaultValue, builderModifier);
        }

        @SuppressWarnings("unchecked")
        default ListConfigProperty<Short> shortListProperty(String key, Supplier<List<Short>> defaultValue, Consumer<ConfigProperty.Builder<List<Short>>> builderModifier) {
            return shortListProperty(key, (ConfigPropertyValidator<Short>) ConfigPropertyValidator.NO_OP, defaultValue, builderModifier);
        }

        default ListConfigProperty<Integer> intListProperty(String key, ConfigPropertyValidator<Integer> elementValidator, Supplier<List<Integer>> defaultValue, Consumer<ConfigProperty.Builder<List<Integer>>> builderModifier) {
            return listProperty(key, ConfigPropertyType.INT, elementValidator, defaultValue, builderModifier);
        }

        @SuppressWarnings("unchecked")
        default ListConfigProperty<Integer> intListProperty(String key, Supplier<List<Integer>> defaultValue, Consumer<ConfigProperty.Builder<List<Integer>>> builderModifier) {
            return intListProperty(key, (ConfigPropertyValidator<Integer>) ConfigPropertyValidator.NO_OP, defaultValue, builderModifier);
        }

        default ListConfigProperty<Float> floatListProperty(String key, ConfigPropertyValidator<Float> elementValidator, Supplier<List<Float>> defaultValue, Consumer<ConfigProperty.Builder<List<Float>>> builderModifier) {
            return listProperty(key, ConfigPropertyType.FLOAT, elementValidator, defaultValue, builderModifier);
        }

        @SuppressWarnings("unchecked")
        default ListConfigProperty<Float> floatListProperty(String key, Supplier<List<Float>> defaultValue, Consumer<ConfigProperty.Builder<List<Float>>> builderModifier) {
            return floatListProperty(key, (ConfigPropertyValidator<Float>) ConfigPropertyValidator.NO_OP, defaultValue, builderModifier);
        }

        default ListConfigProperty<Double> doubleListProperty(String key, ConfigPropertyValidator<Double> elementValidator, Supplier<List<Double>> defaultValue, Consumer<ConfigProperty.Builder<List<Double>>> builderModifier) {
            return listProperty(key, ConfigPropertyType.DOUBLE, elementValidator, defaultValue, builderModifier);
        }

        @SuppressWarnings("unchecked")
        default ListConfigProperty<Double> doubleListProperty(String key, Supplier<List<Double>> defaultValue, Consumer<ConfigProperty.Builder<List<Double>>> builderModifier) {
            return doubleListProperty(key, (ConfigPropertyValidator<Double>) ConfigPropertyValidator.NO_OP, defaultValue, builderModifier);
        }

        @SuppressWarnings("unchecked")
        default ListConfigProperty<Boolean> boolListProperty(String key, Supplier<List<Boolean>> defaultValue, Consumer<ConfigProperty.Builder<List<Boolean>>> builderModifier) {
            return listProperty(key, ConfigPropertyType.BOOLEAN, (ConfigPropertyValidator<Boolean>) ConfigPropertyValidator.NO_OP, defaultValue, builderModifier);
        }

        default <E extends Enum<E>> ListConfigProperty<E> enumListProperty(String key, ConfigPropertyValidator<E> elementValidator, Class<E> enumType, Supplier<List<E>> defaultValue, Consumer<ConfigProperty.Builder<List<E>>> builderModifier) {
            return listProperty(key, ConfigPropertyType.enumType(enumType), elementValidator, defaultValue, builderModifier);
        }

        @SuppressWarnings("unchecked")
        default <E extends Enum<E>> ListConfigProperty<E> enumListProperty(String key, Class<E> enumType, Supplier<List<E>> defaultValue, Consumer<ConfigProperty.Builder<List<E>>> builderModifier) {
            return enumListProperty(key, (ConfigPropertyValidator<E>) ConfigPropertyValidator.NO_OP, enumType, defaultValue, builderModifier);
        }

        default ListConfigProperty<ResourceLocation> resourceLocationListProperty(String key, ConfigPropertyValidator<ResourceLocation> elementValidator, Supplier<List<ResourceLocation>> defaultValue, Consumer<ConfigProperty.Builder<List<ResourceLocation>>> builderModifier) {
            return listProperty(key, ConfigPropertyType.RESOURCE_LOCATION, elementValidator, defaultValue, builderModifier);
        }

        @SuppressWarnings("unchecked")
        default ListConfigProperty<ResourceLocation> resourceLocationListProperty(String key, Supplier<List<ResourceLocation>> defaultValue, Consumer<ConfigProperty.Builder<List<ResourceLocation>>> builderModifier) {
            return resourceLocationListProperty(key, (ConfigPropertyValidator<ResourceLocation>) ConfigPropertyValidator.NO_OP, defaultValue, builderModifier);
        }
    }
}
