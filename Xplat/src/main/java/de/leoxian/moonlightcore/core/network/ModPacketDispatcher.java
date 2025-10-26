package de.leoxian.moonlightcore.core.network;

import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.core.network.clientbound.*;
import de.leoxian.moonlightcore.core.network.serverbound.C2SConfigSyncRequestAcceptedPacket;
import de.leoxian.moonlightcore.core.network.serverbound.ServerPacketHandler;
import de.leoxian.moonlightcore.network.PacketDispatcher;

public class ModPacketDispatcher extends PacketDispatcher {

     public ModPacketDispatcher() {
          super(MoonlightCore.location("main"));
     }

     @Override
     protected void bootstrap() {
         this.registerClientPacket(S2CConfigSyncPacket.ID, S2CConfigSyncPacket.class, S2CConfigSyncPacket.CODEC, ClientPacketHandler::handleConfigSyncPacket);
         this.registerClientPacket(S2CConfigSyncRequestPacket.ID, S2CConfigSyncRequestPacket.class, S2CConfigSyncRequestPacket.CODEC, ClientPacketHandler::handleConfigSyncRequest);
         this.registerClientPacket(S2CAttachmentSyncPacket.ID, S2CAttachmentSyncPacket.class, S2CAttachmentSyncPacket.CODEC, ClientPacketHandler::handleAttachmentSyncPacket);

         this.registerServerPacket(C2SConfigSyncRequestAcceptedPacket.ID, C2SConfigSyncRequestAcceptedPacket.class, C2SConfigSyncRequestAcceptedPacket.CODEC, (s, p, m) -> ServerPacketHandler.handleConfigSyncRequestPacket(p, m));
     }

}
