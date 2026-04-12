package de.realleoxian.moonlightcore.api.config.schema;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ConfigKey {

    ConfigKey child(String key);

    @ApiStatus.Internal
    ConfigKey pop(int count);

    default ConfigKey pop() {
        return pop(1);
    }

    String getComponent(int idx);

    String getFirstComponent();

    String getLastComponent();

    int getComponentCount();

}
