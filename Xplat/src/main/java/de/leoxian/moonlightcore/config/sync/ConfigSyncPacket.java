package de.leoxian.moonlightcore.config.sync;

import de.leoxian.moonlightcore.api.network.MoonlightCustomPacket;
import de.leoxian.moonlightcore.config.ModConfigSpecImpl;
import de.leoxian.moonlightcore.core.MoonlightCore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ConfigSyncPacket(String modId, byte[] data) implements MoonlightCustomPacket.ClientBoundCustomPacket<ConfigSyncPacket> {
    public static final ResourceLocation ID = MoonlightCore.prefix("config_sync");
    public static final Codec CODEC = new Codec();

    public void handle() {
        ((ModConfigSpecImpl) ConfigSyncRegistry.get(this.modId()).orElseThrow()).reload(this.data());
    }

    @Override
    public PacketCodec<ConfigSyncPacket> codec() {
        return CODEC;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static final class Codec implements PacketCodec<ConfigSyncPacket> {

        @Override
        public void encode(FriendlyByteBuf byteBuf, ConfigSyncPacket msg) {
            byteBuf.writeUtf(msg.modId());
            byteBuf.writeByteArray(msg.data());
        }

        @Override
        public ConfigSyncPacket decode(FriendlyByteBuf byteBuf) {
            return new ConfigSyncPacket(byteBuf.readUtf(), byteBuf.readByteArray());
        }

    }
}
