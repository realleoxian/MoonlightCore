package de.realleoxian.moonlightcore.xplat.internal;

import com.mojang.logging.LogUtils;
import de.realleoxian.moonlightcore.api.ModContainer;
import de.realleoxian.moonlightcore.api.event.EventPriority;
import de.realleoxian.moonlightcore.api.event.ServerPlayerNetworkEvents;
import de.realleoxian.moonlightcore.api.ext.MoonlightCoreServerConfigurationPacketListenerExtension;
import de.realleoxian.moonlightcore.api.network.ServerNetworking;
import de.realleoxian.moonlightcore.xplat.config.file.ConfigTracker;
import de.realleoxian.moonlightcore.xplat.internal.network.clientbound.S2CRequestAcceptedModConfigsPacket;
import de.realleoxian.moonlightcore.xplat.internal.network.serverbound.C2SAcceptedModConfigsPacket;
import de.realleoxian.moonlightcore.xplat.internal.network.task.RequestSyncConfigTask;
import de.realleoxian.moonlightcore.xplat.internal.network.task.SyncConfigTask;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.Set;
import java.util.stream.Collectors;

public final class InternalMod {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void initialize(ModContainer modContainer) {
        setupConfig();
    }

    private static void setupConfig() {
        ServerPlayerNetworkEvents.CONFIGURE.subscribe(EventPriority.HIGHEST, (event) -> {
            final var handler = event.handler;
            if (ServerNetworking.canSendConfigurationPayload(handler, S2CRequestAcceptedModConfigsPacket.TYPE)) {
                ((MoonlightCoreServerConfigurationPacketListenerExtension) handler).moonlightcore$addTask(new RequestSyncConfigTask());
            }
        });

        ServerNetworking.registerConfigurationPayload(C2SAcceptedModConfigsPacket.TYPE, C2SAcceptedModConfigsPacket.STREAM_CODEC, (networkListener, server, responseSender, payload) -> {
            final var decoded = decodeSyncableConfigs(payload);
            ((MoonlightCoreServerConfigurationPacketListenerExtension) networkListener).moonlightcore$completeTask(RequestSyncConfigTask.TYPE);
            ((MoonlightCoreServerConfigurationPacketListenerExtension) networkListener).moonlightcore$addTask(new SyncConfigTask(networkListener, decoded));
        });
    }

    private static Set<ResourceLocation> decodeSyncableConfigs(C2SAcceptedModConfigsPacket packet) {
        var clientAcceptedConfigs = packet.acceptedModConfigs();
        var serverSyncables = ConfigTracker.getSyncableConfigs();
        clientAcceptedConfigs.retainAll(serverSyncables);

        if (clientAcceptedConfigs.size() < serverSyncables.size()){
            LOGGER.warn("Client doesn't support all mod configurations.");
            LOGGER.warn("   - Client: {}", clientAcceptedConfigs.size());
            LOGGER.warn("   - Server: {}", serverSyncables.size());
            LOGGER.warn("Missing server configurations on the client:");
            LOGGER.warn(serverSyncables.stream().filter(id -> !clientAcceptedConfigs.contains(id)).map(ResourceLocation::toString).collect(Collectors.joining(", ")));
        }
        return clientAcceptedConfigs;
    }

    private InternalMod() {}
}
