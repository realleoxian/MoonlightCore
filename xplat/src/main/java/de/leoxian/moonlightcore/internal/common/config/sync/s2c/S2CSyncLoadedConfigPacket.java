package de.leoxian.moonlightcore.internal.common.config.sync.s2c;

import de.leoxian.moonlightcore.client.network.ClientConfigurationNetworking;
import de.leoxian.moonlightcore.client.network.ClientPlayNetworking;
import de.leoxian.moonlightcore.common.config.ConfigSchema;
import de.leoxian.moonlightcore.common.config.ConfigValue;
import de.leoxian.moonlightcore.common.config.file.LoadedConfig;
import de.leoxian.moonlightcore.common.config.schema.ConfigKey;
import de.leoxian.moonlightcore.internal.common.config.file.LoadedConfigImpl;
import de.leoxian.moonlightcore.internal.common.config.ConfigRegistry;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record S2CSyncLoadedConfigPacket(Identifier configId, LoadedConfig loadedConfig) implements CustomPacketPayload {
    public static final Type<S2CSyncLoadedConfigPacket> TYPE = new Type<>(Identifier.parse("moonlightcore:sync_config"));
    public static final StreamCodec<FriendlyByteBuf, S2CSyncLoadedConfigPacket> STREAM_CODEC = StreamCodec.of(S2CSyncLoadedConfigPacket::encode, S2CSyncLoadedConfigPacket::decode);

    public static void handleConfiguration(S2CSyncLoadedConfigPacket packet, ClientConfigurationNetworking.Context context) {
        context.enqueueWork(() -> {
            var config = ConfigRegistry.getConfig(packet.configId());
            var loadedConfig = packet.loadedConfig();

            if (config != null) {
                config.loadedConfig().applyFrom(config.schema(), loadedConfig);
            }
        });
    }

    public static void handlePlay(S2CSyncLoadedConfigPacket packet, ClientPlayNetworking.Context context) {
        context.enqueueWork(() -> {
            var config = ConfigRegistry.getConfig(packet.configId());
            var loadedConfig = packet.loadedConfig();

            if (config != null) {
                config.loadedConfig().applyFrom(config.schema(), loadedConfig);
            }
        });
    }

    private static void encode(FriendlyByteBuf byteBuf, S2CSyncLoadedConfigPacket packet) {
        var loadedConfig = packet.loadedConfig();
        byteBuf.writeIdentifier(packet.configId());

        var config = ConfigRegistry.getConfig(packet.configId());
        if (config == null) {
            throw new EncoderException("Unknown config: " + packet.configId());
        }

        List<ConfigValue<?>> allConfigValues = gatherConfigValues(new ArrayList<>(), config.schema());
        byteBuf.writeVarInt(allConfigValues.size());

        for (ConfigValue<?> value : allConfigValues) {
            ConfigKey.STREAM_CODEC.encode(byteBuf, value.key());
            encodeConfigValue(byteBuf, value, loadedConfig);
        }
    }

    private static S2CSyncLoadedConfigPacket decode(FriendlyByteBuf byteBuf) {
        var id = byteBuf.readIdentifier();
        var config = ConfigRegistry.getConfig(id);
        if (config == null) throw new DecoderException("Failed to sync config: '" + id + "'");

        int totalValues = byteBuf.readVarInt();
        Map<ConfigKey, Object> decodedMap = new HashMap<>();
        for (int i = 0; i < totalValues; i++) {
            var key = ConfigKey.STREAM_CODEC.decode(byteBuf);
            var targetValue = findConfigValue(config.schema(), key);
            if (targetValue == null) {
                throw new DecoderException("Failed to sync config '" + id + "': Unknown or mismatched key: " + key);
            }

            decodedMap.put(key, targetValue.type().decodeFromBuf(byteBuf));
        }
        var loadedConfig = new LoadedConfigImpl(decodedMap);
        return new S2CSyncLoadedConfigPacket(id, loadedConfig);
    }

    private static List<ConfigValue<?>> gatherConfigValues(List<ConfigValue<?>> configValues, ConfigSchema schema) {
        configValues.addAll(schema.getConfigValues());
        for (final var child : schema.getSchemas()) gatherConfigValues(configValues, child);
        return configValues;
    }

    private static <T> void encodeConfigValue(FriendlyByteBuf byteBuf, ConfigValue<T> configValue, LoadedConfig loadedConfig) {
        configValue.type().encodeToBuf(byteBuf, loadedConfig.getRaw(configValue));
    }

    private static ConfigValue<?> findConfigValue(ConfigSchema root, ConfigKey key) {
        var current = root;
        for (int i = 0; i < key.getComponentsCount() - 1; i++) {
            current = current.getSection(key.get(i));
            if (current == null) return null;
        }
        return current.getValue(key.lastComponent());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
