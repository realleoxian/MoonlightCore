package de.realleoxian.moonlightcore.xplat.internal.network.task;

import com.mojang.logging.LogUtils;
import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigSchema;
import de.realleoxian.moonlightcore.api.ext.MoonlightCoreServerConfigurationPacketListenerExtension;
import de.realleoxian.moonlightcore.api.network.ServerNetworking;
import de.realleoxian.moonlightcore.xplat.config.file.ConfigTracker;
import de.realleoxian.moonlightcore.xplat.config.sync.ConfigValueSyncChange;
import de.realleoxian.moonlightcore.xplat.internal.network.clientbound.S2CSyncConfigSchemaPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public record SyncConfigTask(ServerConfigurationPacketListenerImpl networkHandler, Set<ResourceLocation> configNames) implements ConfigurationTask {
    public static final Type TYPE = new Type("moonlightcore:sync_config");
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void start(Consumer<Packet<?>> consumer) {
        for (final var configName : configNames) {
            final var modConfig = ConfigTracker.getConfig(ModConfig.Type.SERVER, configName);
            if (modConfig != null) {    // This shouldn't happen
                final var allChanges = new ArrayList<ConfigValueSyncChange>();
                collectAllChanges(modConfig.getSchema(), allChanges);
                consumer.accept(ServerNetworking.createS2CPacket(new S2CSyncConfigSchemaPacket(configName, allChanges)));
            } else {
                LOGGER.warn("Config '{}' not found on server, but client claimed to support it", configName);
            }
        }

        ((MoonlightCoreServerConfigurationPacketListenerExtension) this.networkHandler).moonlightcore$completeTask(TYPE);
    }

    private void collectAllChanges(ConfigSchema schema, List<ConfigValueSyncChange> changes) {
        changes.addAll(schema.createSyncChanges());
        for (final var subSchema : schema.getSchemas()) {
            collectAllChanges(subSchema, changes);
        }
    }

    private void sendSchema(Consumer<Packet<?>> consumer, ResourceLocation configName, ConfigSchema schema) {
        consumer.accept(ServerNetworking.createS2CPacket(new S2CSyncConfigSchemaPacket(configName, schema.createSyncChanges())));
        for (final var subSchema : schema.getSchemas()) {
            sendSchema(consumer, configName, subSchema);
        }
    }

    @Override
    public Type type() {
        return TYPE;
    }
}
