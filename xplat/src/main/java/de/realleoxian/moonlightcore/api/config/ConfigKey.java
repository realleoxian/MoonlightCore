package de.realleoxian.moonlightcore.api.config;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ConfigKey extends Iterable<String>, Comparable<ConfigKey> {
    ConfigKey child(String key);

    String getComponent(int index);

    String getLastComponent();

    int getComponentCount();

    String asFriendlyString();
}
