package de.leoxian.moonlightcore.internal.common.config;

import de.leoxian.moonlightcore.common.EnvironmentSide;
import de.leoxian.moonlightcore.common.config.Config;
import de.leoxian.moonlightcore.common.config.ConfigSchema;
import de.leoxian.moonlightcore.common.event.ServerConfigurationConnectionEvents;
import de.leoxian.moonlightcore.common.event.base.EventPriority;
import de.leoxian.moonlightcore.common.network.PacketDistributor;
import de.leoxian.moonlightcore.common.network.ServerConfigurationNetworking;
import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import de.leoxian.moonlightcore.internal.common.config.file.ConfigFileWatcher;
import de.leoxian.moonlightcore.internal.common.config.sync.s2c.S2CSyncLoadedConfigPacket;
import de.leoxian.moonlightcore.internal.common.config.sync.task.SyncConfigurationTask;
import de.leoxian.moonlightcore.internal.common.util.ModLockHelper;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ConfigRegistry {
    private static final Map<Identifier, ConfigImpl<?>> REGISTRY = new ConcurrentHashMap<>();
    private static final Set<Identifier> SYNCED_CONFIGS = ConcurrentHashMap.newKeySet();

    @SuppressWarnings("unchecked")
    public static <O> Config<O> register(Identifier id, Function<ConfigSchema.Builder, O> factory, boolean synced) {
        return (Config<O>) REGISTRY.computeIfAbsent(id, k -> {
            var lock = ModLockHelper.getOrCreate(id.getNamespace());
            var config = new ConfigImpl<>(id, factory, lock);
            config.load();
            if (synced) {
                SYNCED_CONFIGS.add(id);

                ConfigFileWatcher.register(config, () -> {
                    var currentServer = XplatAbstraction.INSTANCE.getCurrentServer();
                    if (XplatAbstraction.INSTANCE.getEnvironmentSide() == EnvironmentSide.CLIENT && (currentServer != null && currentServer.isDedicatedServer())) return;

                    if (currentServer != null) {
                        currentServer.execute(() -> {
                            config.load();
                            PacketDistributor.sendToAllPlayers(new S2CSyncLoadedConfigPacket(config));
                        });
                    }
                });
            } else {
                ConfigFileWatcher.register(config, config::load);
            }

            return config;
        });
    }

    @Nullable
    public static Config<?> getConfig(Identifier id) {
        return REGISTRY.get(id);
    }

    @UnmodifiableView
    public static Set<Identifier> getSyncableConfigs() {
        return Set.copyOf(SYNCED_CONFIGS);
    }

    private ConfigRegistry() {}
}
