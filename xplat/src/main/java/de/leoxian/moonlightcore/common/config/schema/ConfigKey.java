package de.leoxian.moonlightcore.common.config.schema;

import com.google.common.base.Splitter;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

public final class ConfigKey {
    public static final StreamCodec<FriendlyByteBuf, ConfigKey> STREAM_CODEC = StreamCodec.of(ConfigKey::encodeToBuf, ConfigKey::decodeFromBuf);

    public static void encodeToBuf(FriendlyByteBuf byteBuf, ConfigKey key) {
        byteBuf.writeInt(key.componentsCount);
        for (int idx = 0; idx < key.componentsCount; idx++) {
            byteBuf.writeUtf(key.get(idx));
        }
    }

    public static ConfigKey decodeFromBuf(FriendlyByteBuf byteBuf) {
        var components = new String[byteBuf.readInt()];
        for (int i = 0; i < components.length; i++) {
            components[i] = byteBuf.readUtf();
        }
        return new ConfigKey(components);
    }

    private static final Splitter DOT_SPLITTER = Splitter.on('.');

    private final String[] components;
    private final int componentsCount;
    private final String string;

    public ConfigKey(String[] components) {
        this.components = new String[components.length];
        this.componentsCount = components.length;
        System.arraycopy(components, 0, this.components, 0, this.componentsCount);

        var sj = new StringJoiner(".");
        for (int i = 0; i < this.componentsCount; i++) {
            var comp = this.components[i];
            if (comp == null || comp.isEmpty()) {
                throw new IllegalStateException("ConfigKey component may not be empty or 'null'");
            }
            sj.add(comp);
        }
        this.string = sj.toString();
    }

    public ConfigKey(String path) {
        this (DOT_SPLITTER.splitToStream(path).toArray(String[]::new));
    }

    public ConfigKey child(String key) {
        var newComponents = new String[this.componentsCount + 1];
        System.arraycopy(this.components, 0, newComponents, 0, this.componentsCount);
        newComponents[newComponents.length - 1] = key;
        return new ConfigKey(newComponents);
    }

    public String get(int index) {
        Objects.checkIndex(index, this.componentsCount);
        return this.components[index];
    }

    public String lastComponent() {
        return get(getComponentsCount() - 1);
    }

    public int getComponentsCount() {
        return this.componentsCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj.getClass() != this.getClass()) return false;
        var other = (ConfigKey) obj;
        return Arrays.equals(other.components, this.components);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.components);
    }

    @Override
    public String toString() {
        return this.string;
    }
}
