package de.realleoxian.moonlightcore.xplat.config;

import com.mojang.logging.LogUtils;
import de.realleoxian.moonlightcore.api.MoonlightCore;
import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.api.config.MutableLoadedConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigSchema;
import de.realleoxian.moonlightcore.api.util.DeduplicatingRunnable;
import de.realleoxian.moonlightcore.xplat.config.schema.ConfigSchemaImpl;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.locks.Lock;

public final class ModConfigImpl implements ModConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Duration SAVE_DELAY_TIME = Duration.ofSeconds(2);

    public final Lock lock;
    private final ResourceLocation name;
    private final Type type;
    private final ConfigSchema schema;
    private final Path path;
    private final DeduplicatingRunnable saveTask = new DeduplicatingRunnable(SAVE_DELAY_TIME, this::save);

    public volatile MutableLoadedConfig loadedConfig = DefaultLoadedConfig.INSTANCE;

    @ApiStatus.Internal
    public ModConfigImpl(Lock lock, ResourceLocation name, Type type, ConfigSchema schema) {
        this.lock = lock;
        this.name = name;
        this.type = type;
        this.schema = schema;
        this.path = MoonlightCore.getConfigDirectory().resolve("%s-%s.txt".formatted(name.getNamespace(), name.getPath()));
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
