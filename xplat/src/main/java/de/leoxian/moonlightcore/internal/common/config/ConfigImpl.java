package de.leoxian.moonlightcore.internal.common.config;

import de.leoxian.moonlightcore.common.config.Config;
import de.leoxian.moonlightcore.common.config.ConfigSchema;
import de.leoxian.moonlightcore.common.config.file.LoadedConfig;
import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import de.leoxian.moonlightcore.internal.common.config.file.ConfigFileWriter;
import de.leoxian.moonlightcore.internal.common.config.file.DefaultLoadedConfig;
import de.leoxian.moonlightcore.internal.common.config.file.LoadedConfigImpl;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

public final class ConfigImpl<T> implements Config<T> {
    private final Identifier id;

    private final ConfigSchemaImpl schema;
    private final T instance;

    final Lock lock;

    final LoadedConfig loadedData = new LoadedConfigImpl();
    private Path filePath;

    public ConfigImpl(Identifier id, Function<ConfigSchema.Builder, T> instanceFactory, Lock lock) {
        this.id = id;
        this.lock = lock;

        var rootSchema = new ConfigSchemaImpl(null);
        var builder = new ConfigSchemaImpl.BuilderImpl(rootSchema);
        this.instance = instanceFactory.apply(builder);
        this.schema = rootSchema;
        this.loadedData.applyFrom(this.schema, DefaultLoadedConfig.INSTANCE);
        this.schema.setup(this);
    }

    public void load() {
        this.lock.lock();
        try {
            var dataOpt = LoadedConfigImpl.load(this);
            if (dataOpt.isPresent()) {
                this.loadedData.applyFrom(this.schema, dataOpt.get());
                this.schema.invalidate();
            } else {
                ConfigFileWriter.writeFile(this);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: " + this.filePath(), e);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public T instance() {
        return this.instance;
    }

    @Override
    public Identifier id() {
        return this.id;
    }

    @Override
    public ConfigSchema schema() {
        return this.schema;
    }

    @Override
    public Path filePath() {
        if (this.filePath == null) {
            this.filePath = XplatAbstraction.INSTANCE.getConfigDirectory().resolve(this.id.getNamespace() + "-" + this.id.getPath() + ".txt");
        }
        return this.filePath;
    }

    @Override
    public LoadedConfig loadedConfig() {
        try {
            this.lock.lock();
            return this.loadedData;
        } finally {
            this.lock.unlock();
        }
    }
}
