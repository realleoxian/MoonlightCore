package de.realleoxian.moonlightcore.xplat.config.schema;

import de.realleoxian.moonlightcore.api.config.ConfigKey;
import de.realleoxian.moonlightcore.api.config.metadata.ConfigMetadataType;
import de.realleoxian.moonlightcore.api.config.schema.ConfigValueSerializer;
import de.realleoxian.moonlightcore.api.config.schema.ConfigValueValidator;
import de.realleoxian.moonlightcore.api.config.schema.ListConfigValue;
import de.realleoxian.moonlightcore.api.config.schema.RestartType;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ListConfigValueImpl<T> extends ConfigValueImpl<List<T>> implements ListConfigValue<T> {
    private final Class<T> elementType;
    private final ConfigValueSerializer<T> elementSerializer;
    private final ConfigValueValidator<T> elementValidator;

    ListConfigValueImpl(Map<ConfigMetadataType<?, ?>, Object> metadataType, ConfigKey configKey, ConfigValueSerializer<List<T>> serializer, ConfigValueValidator<List<T>> validator, RestartType restartType, Supplier<List<T>> defaultValue, Class<T> elementType, ConfigValueSerializer<T> elementSerializer, ConfigValueValidator<T> elementValidator) {
        super(metadataType, configKey, serializer, validator, restartType, defaultValue);
        this.elementType = elementType;
        this.elementSerializer = elementSerializer;
        this.elementValidator = elementValidator;
    }

    @Override
    public Class<T> getElementType() {
        return this.elementType;
    }

    @Override
    public ConfigValueSerializer<T> getElementSerializer() {
        return this.elementSerializer;
    }

    @Override
    public ConfigValueValidator<T> getElementValidator() {
        return this.elementValidator;
    }
}
