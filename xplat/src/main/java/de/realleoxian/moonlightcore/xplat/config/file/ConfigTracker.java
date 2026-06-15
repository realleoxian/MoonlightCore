package de.realleoxian.moonlightcore.xplat.config.file;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigSchema;
import de.realleoxian.moonlightcore.xplat.config.ModConfigImpl;
import de.realleoxian.moonlightcore.xplat.config.schema.ConfigSchemaImpl;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public final class ConfigTracker {
    private static final Table<ModConfig.Type, ResourceLocation, ModConfig> CONFIGS_BY_MOD = HashBasedTable.create();
    private static final Map<String, Lock> LOCK_BY_MOD = new HashMap<>();
    private static final ConfigFileWatcher WATCHER_THREAD = ConfigFileWatcher.get();

    static {
        WATCHER_THREAD.start();
    }

    public static void init() {
        // no-op
    }

    public static <O> O register(ModConfig.Type type, ResourceLocation name, Function<ConfigSchema.Builder, O> schemaBuilder) {
        final var lock = LOCK_BY_MOD.computeIfAbsent(name.getNamespace(), k -> new ReentrantLock());
        if (CONFIGS_BY_MOD.contains(type, name)) {
            return null;
        }

        final var builder = new ConfigSchemaImpl.BuilderImpl(Map.of(), null);   // Root schema, neither metadata nor key
        O o = schemaBuilder.apply(builder);
        var config = new ModConfigImpl(lock, name, type, new ConfigSchemaImpl(builder));
        CONFIGS_BY_MOD.put(type, name, config);
        WATCHER_THREAD.register(config.getPath(), config::load);
        config.load();
        return o;
    }

    @Nullable
    public static ModConfig getConfig(ModConfig.Type type, ResourceLocation name) {
        return CONFIGS_BY_MOD.get(type, name);
    }

    @UnmodifiableView
    public static Set<ResourceLocation> getSyncableConfigs() {
        return Collections.unmodifiableSet(CONFIGS_BY_MOD.row(ModConfig.Type.SERVER).keySet());
    }

    private ConfigTracker() {}
}
