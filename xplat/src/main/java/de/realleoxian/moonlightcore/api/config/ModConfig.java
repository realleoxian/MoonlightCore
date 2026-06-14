package de.realleoxian.moonlightcore.api.config;

import de.realleoxian.moonlightcore.api.config.schema.ConfigSchema;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;

public interface ModConfig {
    void clearListeners();

    ConfigSchema getSchema();

    ResourceLocation getName();

    Type getType();

    Path getPath();

    enum Type {
        COMMON,
        CLIENT,
        SERVER
    }
}
