package de.realleoxian.moonlightcore.xplat.config.schema;

import de.realleoxian.moonlightcore.api.config.schema.ConfigPropertyType;
import de.realleoxian.moonlightcore.api.config.schema.ListConfigProperty;
import de.realleoxian.moonlightcore.api.config.schema.validator.ConfigPropertyValidator;

import java.util.List;

public final class ListConfigPropertyImpl<T> extends ConfigPropertyImpl<List<T>> implements ListConfigProperty<T> {
    private final ConfigPropertyValidator<T> elementValidator;
    private final ConfigPropertyType<T> elementType;

    ListConfigPropertyImpl(ConfigPropertyImpl.Builder<List<T>> builder, ConfigPropertyValidator<T> elementValidator, ConfigPropertyType<T> elementType) {
        super(builder);
        this.elementValidator = elementValidator;
        this.elementType = elementType;
    }

    @Override
    public ConfigPropertyValidator<T> elementValidator() {
        return this.elementValidator;
    }

    @Override
    public ConfigPropertyType<T> elementType() {
        return this.elementType;
    }
}
