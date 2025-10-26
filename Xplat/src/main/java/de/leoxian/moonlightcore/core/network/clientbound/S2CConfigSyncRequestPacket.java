package de.leoxian.moonlightcore.core.network.clientbound;

import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.network.CustomPacket;
import de.leoxian.moonlightcore.util.ByteBufCodecs;
import de.leoxian.moonlightcore.util.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record S2CConfigSyncRequestPacket(String modId, String filename) implements CustomPacket<S2CConfigSyncRequestPacket> {
     public static final ResourceLocation ID = MoonlightCore.location("config_sync_request");
     public static final StreamCodec<FriendlyByteBuf, S2CConfigSyncRequestPacket> CODEC = StreamCodec.composite(
             ByteBufCodecs.STRING_UTF8, S2CConfigSyncRequestPacket::modId,
             ByteBufCodecs.STRING_UTF8, S2CConfigSyncRequestPacket::filename,
             S2CConfigSyncRequestPacket::new);

     @Override
     public StreamCodec<FriendlyByteBuf, S2CConfigSyncRequestPacket> codec() {
          return CODEC;
     }

     @Override
     public ResourceLocation id() {
          return ID;
     }
}
