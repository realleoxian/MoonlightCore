package de.leoxian.moonlightcore.internal.core.network.clientbound;

import com.mojang.logging.LogUtils;
import de.leoxian.moonlightcore.common.config.Config;
import de.leoxian.moonlightcore.common.config.ConfigSchema;
import de.leoxian.moonlightcore.common.config.ConfigValue;
import de.leoxian.moonlightcore.common.config.file.LoadedConfig;
import de.leoxian.moonlightcore.common.config.schema.ConfigKey;
import de.leoxian.moonlightcore.internal.common.config.ConfigRegistry;
import de.leoxian.moonlightcore.internal.common.config.file.LoadedConfigImpl;
import de.leoxian.moonlightcore.internal.core.MoonlightCore;
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

public record ClientboundSyncLoadedConfigPacketPayload(Identifier config, LoadedConfig data) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ClientboundSyncLoadedConfigPacketPayload> TYPE = new Type<>(MoonlightCore.id("clientbound_sync_loaded_config"));
	public static final StreamCodec<? super FriendlyByteBuf, ClientboundSyncLoadedConfigPacketPayload> STREAM_CODEC = StreamCodec.of(
			(byteBuf, payload) -> {
				LoadedConfig loadedConfig = payload.data();
				Identifier id = payload.config();
				byteBuf.writeIdentifier(id);

				var config = ConfigRegistry.getConfig(id);
				if (config == null) {
					throw new EncoderException("Unknown config: " + id);
				}

				FriendlyByteBuf subBuf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
				try {
					List<ConfigValue<?>> allConfigValues = gatherConfigValues(new ArrayList<>(), config.schema());
					subBuf.writeVarInt(allConfigValues.size());

					for (ConfigValue<?> value : allConfigValues) {
						ConfigKey.STREAM_CODEC.encode(subBuf, value.key());
						encodeConfigValue(subBuf, value, loadedConfig);
					}

					byteBuf.writeVarInt(subBuf.readableBytes());
					byteBuf.writeBytes(subBuf);
				} finally {
					subBuf.release();
				}
			},
			(byteBuf) -> {
				Identifier id = byteBuf.readIdentifier();
				Config<?> config = ConfigRegistry.getConfig(id);

				int dataLength = byteBuf.readVarInt();
				if (config == null) {
					LogUtils.getLogger().warn("Received configuration sync for unknown ID '{}'. Skipping data safely.", id);
					byteBuf.skipBytes(dataLength);
					return new ClientboundSyncLoadedConfigPacketPayload(id, null);
				}

				FriendlyByteBuf configBuf = new FriendlyByteBuf(byteBuf.readBytes(dataLength));
				try {
					int totalValues = configBuf.readVarInt();
					Map<ConfigKey, Object> decodedMap = new HashMap<>();

					for (int i = 0; i < totalValues; i++) {
						ConfigKey key = ConfigKey.STREAM_CODEC.decode(configBuf);
						ConfigValue<?> targetValue = findConfigValue(config.schema(), key);
						if (targetValue == null) {
							throw new DecoderException("Failed to sync config '" + id + "': Unknown or mismatched key: " + key);
						}

						decodedMap.put(key, targetValue.type().decodeFromBuf(configBuf));
					}

					LoadedConfig loadedConfig = new LoadedConfigImpl(decodedMap);
					return new ClientboundSyncLoadedConfigPacketPayload(id, loadedConfig);
				} finally {
					configBuf.release();
				}
			}
	);

	private static List<ConfigValue<?>> gatherConfigValues(List<ConfigValue<?>> configValues, ConfigSchema schema) {
		configValues.addAll(schema.getConfigValues());
		for (final var child : schema.getSchemas()) {
			gatherConfigValues(configValues, child);
		}
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
