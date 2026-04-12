package de.realleoxian.moonlightcore.impl.internal.network.s2c;

import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.network.PacketType;
import de.realleoxian.moonlightcore.impl.config.ConfigTracker;
import de.realleoxian.moonlightcore.impl.config.LoadedConfigImpl;
import de.realleoxian.moonlightcore.impl.config.ModConfigImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record S2CModConfigSyncPacket(ResourceLocation configId, byte[] data) {
    public static final PacketType<S2CModConfigSyncPacket> TYPE = new PacketType<>(new ResourceLocation("moonlightcore", "mod_config_sync"), S2CModConfigSyncPacket.class, S2CModConfigSyncPacket::writeToBuffer, S2CModConfigSyncPacket::readFromBuffer);

    public static void handle(NetworkHelper.PacketContext context, S2CModConfigSyncPacket packet) {
        context.queueWork(() -> {
            ResourceLocation id = packet.configId();
            ModConfig config = ConfigTracker.getConfig(ModConfig.Type.SERVER, id);
            if (config == null) {
                context.disconnect(Component.translatable("moonlightcore.config.sync.error.unknown", id));
                return;
            }

            ((ModConfigImpl) config).load(LoadedConfigImpl.fromBytes(packet.data()));
        });
    }

    public static S2CModConfigSyncPacket readFromBuffer(FriendlyByteBuf byteBuf) {
        ResourceLocation configId = byteBuf.readResourceLocation();
        byte[] data = byteBuf.readByteArray();

        return new S2CModConfigSyncPacket(configId, data);
    }

    public static void writeToBuffer(FriendlyByteBuf byteBuf, S2CModConfigSyncPacket packet) {
        byteBuf.writeResourceLocation(packet.configId());
        byteBuf.writeByteArray(packet.data());
    }
}
