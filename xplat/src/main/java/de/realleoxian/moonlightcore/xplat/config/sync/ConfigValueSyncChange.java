package de.realleoxian.moonlightcore.xplat.config.sync;

import de.realleoxian.moonlightcore.api.config.ConfigKey;
import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigSchema;
import de.realleoxian.moonlightcore.api.config.schema.ConfigValue;
import de.realleoxian.moonlightcore.xplat.config.ModConfigImpl;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ConfigValueSyncChange(ConfigKey configValueKey, byte[] data) {
    public static final StreamCodec<FriendlyByteBuf, ConfigValueSyncChange> STREAM_CODEC = StreamCodec.composite(
            ConfigKey.STREAM_CODEC, ConfigValueSyncChange::configValueKey,
            ByteBufCodecs.BYTE_ARRAY, ConfigValueSyncChange::data,
            ConfigValueSyncChange::new
    );
    
    public static <T> ConfigValueSyncChange create(ConfigValue<T> configValue) {
        final var serializer = configValue.getSerializer();
        final var byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        serializer.encodeToBuf(byteBuf, configValue.getValue());
        return new ConfigValueSyncChange(configValue.getKey(), byteBuf.array());
    }

    public void tryApply(ModConfig config) {
        final var configValue = ((ModConfigImpl) config).findConfigValue(config.getSchema(), this.configValueKey);
        if (configValue != null) {
            final var serializer = configValue.getSerializer();
            final var byteBuf = new FriendlyByteBuf(Unpooled.copiedBuffer(this.data()));
            configValue.set(serializer.decodeFromBuf(byteBuf), false);  // in-memory changes, don't save
        }
    }
}
