package de.realleoxian.moonlightcore.xplat.config;

import com.mojang.logging.LogUtils;
import de.realleoxian.moonlightcore.api.MoonlightCore;
import de.realleoxian.moonlightcore.api.config.ConfigKey;
import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.api.config.MutableLoadedConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigSchema;
import de.realleoxian.moonlightcore.api.config.schema.ConfigValue;
import de.realleoxian.moonlightcore.api.network.ServerNetworking;
import de.realleoxian.moonlightcore.api.util.DeduplicatingRunnable;
import de.realleoxian.moonlightcore.xplat.config.schema.ConfigSchemaImpl;
import de.realleoxian.moonlightcore.xplat.config.sync.ConfigValueSyncChange;
import de.realleoxian.moonlightcore.xplat.internal.network.clientbound.S2CSyncConfigSchemaPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

public final class ModConfigImpl implements ModConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Duration SAVE_DELAY_TIME = Duration.ofSeconds(2);
    private static final Duration BROADCAST_DELAY_TIME = Duration.ofMillis(500);

    public final Lock lock;
    private final ResourceLocation name;
    private final Type type;
    private final ConfigSchema schema;
    private final Path path;

    private final DeduplicatingRunnable saveTask;

    private final Set<ConfigKey> dirtyKeys = ConcurrentHashMap.newKeySet();
    private final DeduplicatingRunnable broadcastTask;

    public volatile MutableLoadedConfig loadedConfig = DefaultLoadedConfig.INSTANCE;

    @ApiStatus.Internal
    public ModConfigImpl(Lock lock, ResourceLocation name, Type type, ConfigSchema schema) {
        this.lock = lock;
        this.name = name;
        this.type = type;
        this.schema = schema;
        this.path = MoonlightCore.getConfigDirectory().resolve("%s-%s-%s.txt".formatted(name.getNamespace(), name.getPath(), type.name().toLowerCase()));
        this.saveTask = new DeduplicatingRunnable(SAVE_DELAY_TIME, this::save);
        this.broadcastTask = new DeduplicatingRunnable(BROADCAST_DELAY_TIME, this::broadcastDirtyKeys);
        ((ConfigSchemaImpl) schema).accept(this);
    }

    @ApiStatus.Internal
    public void load() {
        this.lock.lock();
        try {
            try {
                this.loadedConfig = new LoadedConfigImpl(this.path);
            } catch (IOException e) {
                LOGGER.error("Failed to load mod config '{}'. Returning to default", this.name);
                this.loadedConfig = DefaultLoadedConfig.INSTANCE;
            }
            ((ConfigSchemaImpl) schema).invalidate();
        } finally {
            this.lock.unlock();
        }
    }

    public void markDirty() {
        this.saveTask.run();
    }

    public void markKeyDirty(ConfigKey key) {
        if (shouldBroadcastToClients()) {
            this.dirtyKeys.add(key);
            this.broadcastTask.run();
        }
    }

    private void broadcastDirtyKeys() {
        if (this.dirtyKeys.isEmpty() || MoonlightCore.getCurrentSever() == null) {
            return;
        }

        final var keysToBroadcast = new HashSet<ConfigKey>(this.dirtyKeys);
        this.dirtyKeys.clear();
        final var changes = new ArrayList<ConfigValueSyncChange>();
        for (ConfigKey key : keysToBroadcast) {
            final var configValue = findConfigValue(this.schema, key);
            if (configValue != null) {
                try {
                    changes.add(ConfigValueSyncChange.create(configValue));
                } catch (Exception e) {
                    LOGGER.error("Failed to create sync change for key: {}", key.asFriendlyString(), e);
                }
            } else {
                LOGGER.warn("Could not find config value for dirty key: {}", key.asFriendlyString());
            }
        }

        if (!changes.isEmpty()) {
            final var packet = new S2CSyncConfigSchemaPacket(this.name, changes);
            for (ServerPlayer player : MoonlightCore.getCurrentSever().getPlayerList().getPlayers()) {
                player.connection.send(ServerNetworking.createS2CPacket(packet));
            }
        }
    }

    private boolean shouldBroadcastToClients() {
        return this.type == Type.SERVER && MoonlightCore.getCurrentSever() != null;
    }

    @SuppressWarnings("unchecked")
    public  <T> ConfigValue<T> findConfigValue(ConfigSchema schema, ConfigKey key) {
        for (final var configValue : schema.getValues()) {
            if (configValue.getKey().equals(key)) {
                return (ConfigValue<T>) configValue;
            }
        }

        for (final var subSchema : schema.getSchemas()) {
            final var configValue = findConfigValue(subSchema, key);
            if (configValue != null) {
                return (ConfigValue<T>) configValue;
            }
        }

        return null;
    }

    @Override
    public void clearListeners() {
        ((ConfigSchemaImpl) this.schema).clearListeners();
        for (final var schema : this.schema.getSchemas())((ConfigSchemaImpl) schema).clearListeners();
    }

    @Override
    public ConfigSchema getSchema() {
        return this.schema;
    }

    @Override
    public ResourceLocation getName() {
        return this.name;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    @ApiStatus.Internal
    private void save() {
        try {
            ConfigSerializer.writeToFile(this);
        } catch (IOException e) {
            LOGGER.error("Failed to save config file '{}'", this.path, e);
        }
    }
}
