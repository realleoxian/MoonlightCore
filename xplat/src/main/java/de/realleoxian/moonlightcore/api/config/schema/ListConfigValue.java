package de.realleoxian.moonlightcore.api.config.schema;

import java.util.List;

public interface ListConfigValue<T> extends ConfigValue<List<T>> {
    Class<T> getElementType();

    ConfigValueSerializer<T> getElementSerializer();

    ConfigValueValidator<T> getElementValidator();
}
