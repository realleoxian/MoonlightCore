package de.leoxian.moonlightcore.core.network.clientbound;

import de.leoxian.moonlightcore.attachment.sync.AttachmentChange;
import de.leoxian.moonlightcore.config.ConfigManager;
import de.leoxian.moonlightcore.config.ConfigSerializer;
import de.leoxian.moonlightcore.config.ModConfigSpec;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.core.network.serverbound.C2SConfigSyncRequestAcceptedPacket;
import net.minecraft.client.Minecraft;

public class ClientPacketHandler {

    public static void handleConfigSyncPacket(S2CConfigSyncPacket packet) {
        String modId = packet.modId();
        String filename = packet.filename();
        byte[] data = packet.data();

        if(ConfigManager.isSyncedSpec(modId, filename)) {
            ModConfigSpec spec = ConfigManager.getSyncedSpec(modId, filename);
            ConfigSerializer.readFromBytes(data, spec);
        }
    }

    public static void handleConfigSyncRequest(S2CConfigSyncRequestPacket packet) {
        String modId = packet.modId();
        String fileName = packet.filename();

        if(ConfigManager.isSyncedSpec(modId, fileName)) {
            MoonlightCore.PACKET_DISPATCHER.sendToServer(new C2SConfigSyncRequestAcceptedPacket(modId, fileName));
        }
    }

    public static void handleAttachmentSyncPacket(S2CAttachmentSyncPacket packet) {
        for(AttachmentChange change : packet.changes()) {
            change.applyChanges(Minecraft.getInstance().level);
        }
    }

}
