package de.realleoxian.moonlightcore.api.config.schema;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ConfigKey {
    ConfigKey child(String key);

    String getComponent(int index);

    int componentsCount();
}
