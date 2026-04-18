package de.realleoxian.moonlightcore.impl.config;

import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.api.event.EventPriority;
import de.realleoxian.moonlightcore.api.event.ServerPlayerNetworkEvents;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.core.network.s2c.S2CModConfigSyncPacket;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ConfigTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger("moonlightcore-config-api");
    private static final EnumMap<ModConfig.Type, Map<ResourceLocation, ModConfig>> REGISTERED_CONFIGS = new EnumMap<>(ModConfig.Type.class);
    private static final ConfigFileWatcherThread WATCHER_THREAD;

    static {
        for (ModConfig.Type type : ModConfig.Type.values()) {
            REGISTERED_CONFIGS.put(type, new HashMap<>());
        }

        try {
            WATCHER_THREAD = new ConfigFileWatcherThread();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create Config File Watcher", e);
        }

        ServerPlayerNetworkEvents.LOGGED_IN.subscribe(EventPriority.HIGHEST,  (handler, sender, server) -> {
            REGISTERED_CONFIGS.get(ModConfig.Type.SERVER).values().forEach((config) -> {
                try {
                    byte[] data = Files.readAllBytes(config.getFilePath());
                    NetworkHelper.get().sendToPlayer(handler.getPlayer(), new S2CModConfigSyncPacket(config.getId(), data));
                } catch (IOException e) {
                    LOGGER.error("Failed to send config sync packet for {}", config.getId());
                }
            });
        });
    }

    public static void startTracking() {
        WATCHER_THREAD.start();
    }

    public static Optional<ModConfig> getConfig(ModConfig.Type type, ResourceLocation id) {
        return Optional.ofNullable(REGISTERED_CONFIGS.get(type).get(id));
    }

    @ApiStatus.Internal
    static void register(ModConfig config) {
        if (REGISTERED_CONFIGS.get(config.getType()).putIfAbsent(config.getId(), config) != null) {
            throw new IllegalStateException("Duplicated mod config with id '" + config.getId() + "'");
        }

        WATCHER_THREAD.addCallback(config, () -> ((ModConfigImpl) config).needsReload.compareAndSet(false, true));
    }
}
