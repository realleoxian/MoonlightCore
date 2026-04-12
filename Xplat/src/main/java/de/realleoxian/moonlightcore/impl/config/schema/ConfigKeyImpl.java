package de.realleoxian.moonlightcore.impl.config.schema;

import com.google.common.base.Splitter;
import de.realleoxian.moonlightcore.api.config.schema.ConfigKey;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Arrays;
import java.util.Objects;

public final class ConfigKeyImpl implements ConfigKey {
    private static final Splitter DOT_SPLITTER = Splitter.on('.');

    public static ConfigKey decodeFromBuf(FriendlyByteBuf byteBuf) {
        int length = byteBuf.readVarInt();

        String[] components = new String[length];
        for (int i = 0; i < length; i++) {
            components[i] = byteBuf.readUtf();
        }
        return new ConfigKeyImpl(components);
    }

    public static void encodeToBuf(ConfigKey key, FriendlyByteBuf byteBuf) {
        byteBuf.writeVarInt(key.getComponentCount());

        for (int i = 0; i < key.getComponentCount(); i++) {
            String str = key.getComponent(i);
            byteBuf.writeUtf(str);
        }
    }

    private final String[] components;
    private final int componentCount;
    private final String str;

    public ConfigKeyImpl(String str) {
        this(DOT_SPLITTER.splitToList(str).toArray(String[]::new));
    }

    public ConfigKeyImpl(String[] components) {
        this.components = new String[components.length];
        System.arraycopy(components, 0, this.components, 0, components.length);
        this.componentCount = components.length;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.componentCount; i++) {
            String str = this.components[i];
            if (str == null || str.isEmpty()) {
                throw new IllegalArgumentException("ConfigKey cannot have empty or null components");
            }

            if (i == 0) {
                builder.append(str);
                continue;
            }

            builder.append(".").append(str);
        }
        this.str = builder.toString();
    }

    @Override
    public ConfigKey child(String key) {
        String[] keyComponents = new String[this.componentCount + 1];
        System.arraycopy(this.components, 0, keyComponents, 0, this.components.length);
        keyComponents[keyComponents.length - 1] = key;

        return new ConfigKeyImpl(keyComponents);
    }

    @Override
    public ConfigKey pop(int count) {
        if (count <= 0) throw new IllegalArgumentException("Cannot pop 0 or less components from config key");
        if (count > this.componentCount) throw new IllegalArgumentException("Cannot pop more than %d components from config key".formatted(this.componentCount));

        String[] keyComponents = new String[this.componentCount - count];
        System.arraycopy(this.components, 0, keyComponents, 0, this.components.length - count);
        return new ConfigKeyImpl(keyComponents);
    }

    @Override
    public String getComponent(int idx) {
        Objects.checkIndex(idx, this.componentCount);
        return this.components[idx];
    }

    @Override
    public String getFirstComponent() {
        return this.components[0];
    }

    @Override
    public String getLastComponent() {
        return this.components[componentCount];
    }

    @Override
    public int getComponentCount() {
        return componentCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ConfigKeyImpl other)) return false;

        return Arrays.equals(this.components, other.components);
    }

    @Override
    public String toString() {
        return this.str;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.components);
    }
}
