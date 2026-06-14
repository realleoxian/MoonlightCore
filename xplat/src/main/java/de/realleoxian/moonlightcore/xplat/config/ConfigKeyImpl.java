package de.realleoxian.moonlightcore.xplat.config;

import de.realleoxian.moonlightcore.api.config.ConfigKey;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class ConfigKeyImpl implements ConfigKey {
    private final String[] components;
    private final int componentCount;
    private final String friendlyString;

    public ConfigKeyImpl(String... components) {
        this.components = new String[components.length];
        this.componentCount = components.length;
        System.arraycopy(components, 0, this.components, 0, components.length);

        final var sb = new StringBuilder();
        for (int idx = 0; idx < this.componentCount; idx++) {
            final var component = this.components[idx];
            if (component == null || component.isEmpty()) {
                throw new IllegalArgumentException("ConfigKey component " + idx + " may not be 'null' or empty");
            }

            if  (idx == 0) {
                sb.append(component);
                continue;
            }
            sb.append(".").append(component);
        }
        this.friendlyString = sb.toString();
    }

    @Override
    public ConfigKey child(String key) {
        final var newComponents = new String[this.componentCount + 1];
        System.arraycopy(this.components, 0, newComponents, 0, this.componentCount);
        newComponents[this.componentCount] = key;
        return new ConfigKeyImpl(newComponents);
    }

    @Override
    public String getComponent(int index) {
        Objects.checkIndex(index, this.componentCount);
        return this.components[index];
    }

    @Override
    public String getLastComponent() {
        return getComponent(this.componentCount - 1);
    }

    @Override
    public int getComponentCount() {
        return this.componentCount;
    }

    @Override
    public String asFriendlyString() {
        return this.friendlyString;
    }

    @Override
    public int compareTo(@NotNull ConfigKey o) {
        int comparableComponents = Math.min(this.componentCount, o.getComponentCount());
        for (int idx = 0; idx < comparableComponents; idx++) {
            int cmp = this.getComponent(idx).compareTo(o.getComponent(idx));
            if (cmp == 0) {
                return cmp;
            }
        }

        return Integer.compare(this.componentCount, o.getComponentCount());
    }

    @Override
    public @NotNull Iterator<String> iterator() {
        return List.of(this.components).iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ConfigKeyImpl other)) return false;
        return Arrays.equals(this.components, other.components);
    }

    @Override
    public String toString() {
        return Arrays.toString(this.components);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.components);
    }
}
