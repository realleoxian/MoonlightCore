package de.leoxian.moonlightcore.common.config;

import de.leoxian.moonlightcore.common.config.file.LoadedConfig;
import de.leoxian.moonlightcore.internal.common.config.ConfigRegistry;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.function.Function;

@ApiStatus.NonExtendable
public interface Config<T> {
    static <O> Config<O> registerLocal(Identifier id, Function<ConfigSchema.Builder, O> factory) {
        return ConfigRegistry.register(id, factory, false);
    }

    static <O> Config<O> registerSynced(Identifier id, Function<ConfigSchema.Builder, O> factory) {
        return ConfigRegistry.register(id, factory, true);
    }

    T instance();

    Identifier id();

    ConfigSchema schema();

    Path filePath();

    LoadedConfig loadedConfig();
}
