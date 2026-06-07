package de.realleoxian.moonlightcore.xplat.config;

import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.api.config.internal.LoadedConfig;
import de.realleoxian.moonlightcore.api.config.internal.MutableLoadedConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigSchema;
import de.realleoxian.moonlightcore.xplat.config.schema.ConfigKeyImpl;
import de.realleoxian.moonlightcore.xplat.config.schema.ConfigSchemaImpl;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public final class ConfigTracker {
    private static final EnumMap<ModConfig.Type, Map<ResourceLocation, ModConfig>> CONFIGS = new EnumMap<>(ModConfig.Type.class);
    private static final EnumMap<ModConfig.Type, Map<ResourceLocation, LoadedConfig>> LOADED_CONFIGS = new EnumMap<>(ModConfig.Type.class);
    private static final Map<String, ReentrantLock> LOCKS_BY_MOD = new HashMap<>();

    static {
        for (ModConfig.Type type : ModConfig.Type.values()) {
            CONFIGS.put(type, new HashMap<>());
            LOADED_CONFIGS.put(type, new HashMap<>());
        }
    }

    public static void setConfigData(ModConfig.Type type, ResourceLocation configName, MutableLoadedConfig config) {
        final var modConfig = CONFIGS.get(type).get(configName);
        if (modConfig == null) {
            throw new IllegalArgumentException("Cannot assign config data to unknown mod config: " + type + " - '" + configName + "'");
        }

        LOADED_CONFIGS.get(type).put(configName, config);
        modConfig.apply(config);
    }

    public static LoadedConfig getLoadedConfig(ModConfig.Type type, ResourceLocation name) {
        final var config = LOADED_CONFIGS.get(type).get(name);
        if (config == null) {
            throw new IllegalArgumentException("Unknown config '" + name + "' of type " + type);
        }
        return config;
    }

    public static void registerConfig(ModConfig.Type type, ResourceLocation name, Consumer<ConfigSchema.Builder> schemaBuilder) {
        final var lock = LOCKS_BY_MOD.computeIfAbsent(name.getNamespace(), k -> new ReentrantLock());
        ConfigSchemaImpl.Builder builder = new ConfigSchemaImpl.Builder(new ConfigKeyImpl("root"));
        schemaBuilder.accept(builder);

        final var config = new ModConfigImpl(name, type, new ConfigSchemaImpl(builder), lock);
        if (CONFIGS.get(type).putIfAbsent(name, config) != null) {
            throw new IllegalArgumentException("Duplicated config with id '" + name + "'");
        }
    }

    private ConfigTracker() {}
}
