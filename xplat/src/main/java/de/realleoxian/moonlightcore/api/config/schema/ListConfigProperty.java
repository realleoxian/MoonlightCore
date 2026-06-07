package de.realleoxian.moonlightcore.api.config.schema;

import de.realleoxian.moonlightcore.api.config.schema.validator.ConfigPropertyValidator;

import java.util.List;

public interface ListConfigProperty<T> extends ConfigProperty<List<T>> {
    ConfigPropertyValidator<T> elementValidator();

    ConfigPropertyType<T> elementType();
}
