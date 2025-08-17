package de.leowgc.moonlightcore.config.sync;

import com.mojang.logging.LogUtils;
import de.leowgc.moonlightcore.api.network.MoonlightCustomPacket;
import de.leowgc.moonlightcore.config.ModConfigSpecImpl;
import de.leowgc.moonlightcore.core.MoonlightCore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ConfigSyncPacket(String modId, byte[] data) implements MoonlightCustomPacket.ClientBoundCustomPacket<ConfigSyncPacket> {
    public static final ResourceLocation ID = MoonlightCore.prefix("config_sync");
    public static final Codec CODEC = new Codec();

    public void handle() {
        LogUtils.getLogger().debug("Syncing {} mod server config", modId);
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
