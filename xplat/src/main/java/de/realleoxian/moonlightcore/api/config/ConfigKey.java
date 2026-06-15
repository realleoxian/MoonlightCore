package de.realleoxian.moonlightcore.api.config;

import de.realleoxian.moonlightcore.xplat.config.ConfigKeyImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ConfigKey extends Iterable<String>, Comparable<ConfigKey> {
    StreamCodec<FriendlyByteBuf, ConfigKey> STREAM_CODEC = ConfigKeyImpl.STREAM_CODEC;

    ConfigKey child(String key);

    String getComponent(int index);

    String getLastComponent();

    int getComponentCount();

    String asFriendlyString();
}
