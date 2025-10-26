package de.leoxian.moonlightcore.core.network.clientbound;

import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.network.CustomPacket;
import de.leoxian.moonlightcore.util.ByteBufCodecs;
import de.leoxian.moonlightcore.util.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record S2CConfigSyncPacket(String modId, String filename, byte[] data) implements CustomPacket<S2CConfigSyncPacket> {
     public static final ResourceLocation ID = MoonlightCore.location("config_sync");
     public static final StreamCodec<FriendlyByteBuf, S2CConfigSyncPacket> CODEC = StreamCodec.composite(
             ByteBufCodecs.STRING_UTF8, S2CConfigSyncPacket::modId,
             ByteBufCodecs.STRING_UTF8, S2CConfigSyncPacket::filename,
             ByteBufCodecs.BYTE_ARRAY, S2CConfigSyncPacket::data, S2CConfigSyncPacket::new);

     @Override
     public StreamCodec<FriendlyByteBuf, S2CConfigSyncPacket> codec() {
          return CODEC;
     }

     @Override
     public ResourceLocation id() {
          return ID;
     }
}
