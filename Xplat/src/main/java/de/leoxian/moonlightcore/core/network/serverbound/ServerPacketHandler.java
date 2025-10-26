package de.leoxian.moonlightcore.core.network.serverbound;

import com.mojang.logging.LogUtils;
import de.leoxian.moonlightcore.config.ConfigManager;
import de.leoxian.moonlightcore.config.ConfigSerializer;
import de.leoxian.moonlightcore.config.ModConfigSpec;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.core.network.clientbound.S2CConfigSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public class ServerPacketHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void handleConfigSyncRequestPacket(ServerPlayer player, C2SConfigSyncRequestAcceptedPacket packet) {
        LOGGER.warn("Receiving config sync request packet");

        String modId = packet.modId();
        String filename = packet.filename();

        if(ConfigManager.isSyncedSpec(modId, filename)) {
            ModConfigSpec spec = ConfigManager.getSyncedSpec(modId, filename);
            byte[] data = ConfigSerializer.readToBytes(spec);

            MoonlightCore.PACKET_DISPATCHER.sendToPlayer(player, new S2CConfigSyncPacket(packet.modId(), packet.filename(), data));
        }
    }

}
