package de.realleoxian.moonlightcore.api.config;

import de.realleoxian.moonlightcore.api.config.internal.MutableLoadedConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigSchema;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ModConfig {
    void apply(MutableLoadedConfig config);

    void validate();

    ResourceLocation name();

    Type type();

    ConfigSchema schema();

    enum Type {
        COMMON,
        CLIENT,
        SERVER
    }
}
