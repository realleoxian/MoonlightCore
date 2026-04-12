package de.realleoxian.moonlightcore.impl.config;

import com.mojang.logging.LogUtils;
import de.realleoxian.moonlightcore.api.MoonlightCore;
import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.api.config.internal.LoadedConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigSchema;
import de.realleoxian.moonlightcore.impl.config.schema.ConfigSchemaImpl;
import de.realleoxian.moonlightcore.api.misc.DelayedRunner;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public final class ModConfigImpl implements ModConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Duration SAVE_DELAY_TIME = Duration.ofSeconds(2);

    public static <O> O configure(ModConfig.Type type, ResourceLocation id, Function<ConfigSchema.Builder, O> factory) {
        ConfigSchemaImpl.BuilderImpl builder = new ConfigSchemaImpl.BuilderImpl();
        O o = factory.apply(builder);

        if (!builder.isBuilt())
            throw new IllegalStateException("Config schema hasn't been built");

        ModConfig config = new ModConfigImpl(type, id, new ConfigSchemaImpl(builder));
        ConfigTracker.register(config);
        config.register();
        return o;
    }

    private final ModConfig.Type type;
    private final ResourceLocation id;
    private final ConfigSchemaImpl schema;
    private final Path filePath;

    @ApiStatus.Internal
    public final Lock lock = new ReentrantLock();
    final AtomicBoolean needsReload = new AtomicBoolean(false);
    private final DelayedRunner delayedSave = new DelayedRunner(SAVE_DELAY_TIME);

    private ModConfigImpl(ModConfig.Type type, ResourceLocation id, ConfigSchemaImpl schema) {
        this.type = type;
        this.id = id;
        this.schema = schema;
        this.filePath = MoonlightCore.getConfigDirectory().resolve("%s-%s.ezc".formatted(getId().getNamespace(), getId().getPath()));
    }

    @ApiStatus.Internal
    public void markDirty() {
        this.delayedSave.run(this.schema::save);
    }

    @Override
    public void register() { // TODO: Invoke this
        if (Files.exists(this.filePath)) {
            loadIfNeeded();
        }
        this.schema.setParent(this);
        this.schema.save();

        ConfigTracker.register(this);
    }

    @ApiStatus.Internal
    public void load(LoadedConfig config) {
        this.schema.accept(config);
    }

    @Override
    public void loadIfNeeded() {
        if (!needsReload.compareAndSet(true, false)) return;

        if (Files.exists(this.filePath)) {
            try {
                load(new LoadedConfigImpl(Files.readAllLines(this.filePath)));
            } catch (IOException e) {
                LOGGER.error("Failed load config '{}' ({})", this.id, this.filePath, e);
            }
        }
    }

    @Override
    public ConfigSchema getSchema() {
        return this.schema;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public Path getFilePath() {
        return filePath;
    }
}
