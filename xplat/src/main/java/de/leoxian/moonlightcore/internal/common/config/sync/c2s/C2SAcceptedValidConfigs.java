package de.leoxian.moonlightcore.internal.common.config.sync.c2s;

import com.mojang.logging.LogUtils;
import de.leoxian.moonlightcore.common.event.ServerPlayConnectionEvents;
import de.leoxian.moonlightcore.common.network.PacketDistributor;
import de.leoxian.moonlightcore.common.network.ServerConfigurationNetworking;
import de.leoxian.moonlightcore.common.network.ServerPlayNetworking;
import de.leoxian.moonlightcore.internal.common.config.ConfigRegistry;
import de.leoxian.moonlightcore.internal.common.config.sync.s2c.S2CSyncLoadedConfigPacket;
import de.leoxian.moonlightcore.internal.common.config.sync.task.RequestValidConfigsTask;
import de.leoxian.moonlightcore.internal.common.config.sync.task.SyncConfigurationTask;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record C2SAcceptedValidConfigs(Set<Identifier> validConfigs) implements CustomPacketPayload {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Type<C2SAcceptedValidConfigs> TYPE = new Type<>(Identifier.parse("moonlightcore:accepted_valid_configs"));
    public static final StreamCodec<ByteBuf, C2SAcceptedValidConfigs> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(HashSet::new, Identifier.STREAM_CODEC), C2SAcceptedValidConfigs::validConfigs,
            C2SAcceptedValidConfigs::new
    );

    public static void handleConfiguration(C2SAcceptedValidConfigs packet, ServerConfigurationNetworking.Context context) {
        context.enqueueWork(() -> {
            var packetListener = context.packetListener();
            var decoded = decodeSyncableConfigs(packet);
            if (ServerConfigurationNetworking.canSend(packetListener, S2CSyncLoadedConfigPacket.TYPE)) {
                ServerConfigurationNetworking.addTask(packetListener, new SyncConfigurationTask(packetListener, decoded));
            }
            ServerConfigurationNetworking.completeTask(packetListener, RequestValidConfigsTask.TYPE);
        });
    }

    public static void handlePlay(C2SAcceptedValidConfigs packet, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            var player = context.player();
            var decoded = decodeSyncableConfigs(packet);
            for (final var configId : decoded) {
                var config = ConfigRegistry.getConfig(configId);
                if (config == null) {
                    continue;
                }

                if (ServerPlayNetworking.canSendToPlayer(player, S2CSyncLoadedConfigPacket.TYPE)) {
                    PacketDistributor.sendToPlayer(context.player(), new S2CSyncLoadedConfigPacket(config));
                }
            }
        });
    }

    private static Set<Identifier> decodeSyncableConfigs(C2SAcceptedValidConfigs packet) {
        var clientValidConfigs = packet.validConfigs();
        var serverValidConfigs = ConfigRegistry.getSyncableConfigs();
        clientValidConfigs.retainAll(serverValidConfigs);

        if (clientValidConfigs.size() < serverValidConfigs.size()) {
            LOGGER.warn("Client doesn't support all mod configurations.");
            LOGGER.warn("   - Client: {}", clientValidConfigs.size());
            LOGGER.warn("   - Server: {}", serverValidConfigs.size());
            LOGGER.warn("Missing server configurations on the client:");
            LOGGER.warn(serverValidConfigs.stream().filter(id -> !clientValidConfigs.contains(id)).map(Identifier::toString).collect(Collectors.joining(", ")));
        }
        return clientValidConfigs;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
