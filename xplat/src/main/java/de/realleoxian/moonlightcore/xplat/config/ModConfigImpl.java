package de.realleoxian.moonlightcore.xplat.config;

import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.api.config.internal.LoadedConfig;
import de.realleoxian.moonlightcore.api.config.internal.MutableLoadedConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigSchema;
import de.realleoxian.moonlightcore.xplat.config.internal.DefaultLoadedConfig;
import de.realleoxian.moonlightcore.xplat.config.schema.ConfigSchemaImpl;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.locks.Lock;

public final class ModConfigImpl implements ModConfig {
    private final ResourceLocation name;
    private final Type type;
    private final ConfigSchema schema;
    public final Lock lock;

    private LoadedConfig config;

    ModConfigImpl(ResourceLocation name, Type type, ConfigSchema schema, Lock lock) {
        this.name = name;
        this.type = type;
        this.schema = schema;
        this.config = DefaultLoadedConfig.INSTANCE;
        this.lock = lock;
    }

    @Override
    public void apply(MutableLoadedConfig config) {
        this.lock.lock();
        this.config = config;
        this.lock.unlock();
    }

    @Override
    public void validate() {
        ((ConfigSchemaImpl) this.schema).validate(this.config, this);
    }

    @Override
    public ResourceLocation name() {
        return this.name;
    }

    @Override
    public Type type() {
        return this.type;
    }

    @Override
    public ConfigSchema schema() {
        return this.schema;
    }
}
