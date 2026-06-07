package de.realleoxian.moonlightcore.xplat.config.schema;

import com.google.common.base.Preconditions;
import de.realleoxian.moonlightcore.api.config.schema.ConfigKey;

import java.util.Arrays;
import java.util.Objects;

public final class ConfigKeyImpl implements ConfigKey {
    private final String[] components;

    public ConfigKeyImpl(String... components) {
        Preconditions.checkArgument(components.length > 0, "Config key components need to be at least 1");
        this.components = new String[components.length];
        System.arraycopy(components, 0, this.components, 0, components.length);
    }

    public ConfigKeyImpl(String str) {
        this (str.split("\\."));
    }

    @Override
    public ConfigKey child(String key) {
        final var newComponents = new String[components.length + 1];
        System.arraycopy(this.components, 0, newComponents, 0, this.components.length);
        newComponents[newComponents.length - 1] = key;

        return new ConfigKeyImpl(newComponents);
    }

    @Override
    public String getComponent(int index) {
        Objects.checkIndex(index, this.components.length);
        return this.components[index];
    }

    @Override
    public int componentsCount() {
        return this.components.length;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj.getClass() != this.getClass()) return false;
        var other = (ConfigKeyImpl) obj;
        return Arrays.equals(other.components, this.components);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (int i = 0; i < this.components.length; i++) {
            if (i == 0) {
                sb.append(components[i]);
                continue;
            }

            sb.append(".").append(components[i]);
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.components);
    }
}
