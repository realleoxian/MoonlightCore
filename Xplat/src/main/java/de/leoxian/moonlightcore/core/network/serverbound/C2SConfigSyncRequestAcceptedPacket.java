package de.leoxian.moonlightcore.core.network.serverbound;

import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.network.CustomPacket;
import de.leoxian.moonlightcore.util.ByteBufCodecs;
import de.leoxian.moonlightcore.util.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record C2SConfigSyncRequestAcceptedPacket(String modId, String filename) implements CustomPacket<C2SConfigSyncRequestAcceptedPacket> {
     public static final ResourceLocation ID = MoonlightCore.location("config_sync_accepted");
     public static final StreamCodec<FriendlyByteBuf, C2SConfigSyncRequestAcceptedPacket> CODEC = StreamCodec.composite(
             ByteBufCodecs.STRING_UTF8, C2SConfigSyncRequestAcceptedPacket::modId,
             ByteBufCodecs.STRING_UTF8, C2SConfigSyncRequestAcceptedPacket::filename, C2SConfigSyncRequestAcceptedPacket::new);

     @Override
     public StreamCodec<FriendlyByteBuf, C2SConfigSyncRequestAcceptedPacket> codec() {
          return CODEC;
     }

     @Override
     public ResourceLocation id() {
          return ID;
     }
}
