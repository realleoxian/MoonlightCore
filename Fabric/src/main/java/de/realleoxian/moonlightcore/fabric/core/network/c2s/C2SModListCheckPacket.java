package de.realleoxian.moonlightcore.fabric.core.network.c2s;

import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.MoonlightCore;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.network.PacketType;
import de.realleoxian.moonlightcore.fabric.network.FabricNetworkHelperImpl;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.HashMap;
import java.util.Map;

public record C2SModListCheckPacket(Map<String, FabricNetworkHelperImpl.ChannelVersion> channels) {
    public static final PacketType<C2SModListCheckPacket> TYPE = new PacketType<>(new ResourceLocation("moonlightcore:mod_list_check"), C2SModListCheckPacket.class, C2SModListCheckPacket::encodeToBuf, C2SModListCheckPacket::decodeFromBuf);

    public static void handle(C2SModListCheckPacket packet, NetworkHelper.PacketContext<ServerGamePacketListenerImpl> context) {
        FabricNetworkHelperImpl networkHelper = (FabricNetworkHelperImpl) MoonlightCore.getNetworkHelper();

        var clientChannels = packet.channels();
        for (var clientEntry : clientChannels.entrySet()) {
            var namespace = clientEntry.getKey();
            var clientVersion = clientEntry.getValue();
            var optServerVersion = networkHelper.getChannelVersion(namespace, EnvSide.SERVER);

            if (optServerVersion.isEmpty()) {
                if (clientVersion.requireRemote()) {
                    context.disconnect(Component.translatable("moonlightcore.network.fabric.mod_list_check.missing_on_server",
                            Component.literal(namespace).withStyle(ChatFormatting.RED)));
                    return;
                }

                continue;
            }

            var serverVersion = optServerVersion.get();
            if (!clientVersion.protocolVersion().equals(serverVersion.protocolVersion())) {
                context.disconnect(Component.translatable("moonlightcore.network.fabric.mod_list_check.mod_version_mismatch",
                        Component.literal(namespace).withStyle(ChatFormatting.GOLD),
                        Component.literal(serverVersion.modVersion()).withStyle(ChatFormatting.GREEN),
                        Component.literal(clientVersion.modVersion()).withStyle(ChatFormatting.RED)));
                return;
            }
        }

        for (var namespace : networkHelper.getRegisteredMods()) {
            var serverVersion = networkHelper.getChannelVersion(namespace, EnvSide.SERVER).orElseThrow();

            if (serverVersion.requireRemote() && !clientChannels.containsKey(namespace)) {
                context.disconnect(Component.translatable("moonlightcore.network.fabric.mod_list_check.missing_on_client",
                        Component.literal(namespace).withStyle(ChatFormatting.RED),
                        Component.literal(namespace).withStyle(ChatFormatting.GOLD),
                        Component.literal(serverVersion.modVersion()).withStyle(ChatFormatting.GREEN)));
                return;
            }
        }
    }

    private static void encodeToBuf(C2SModListCheckPacket packet, FriendlyByteBuf byteBuf) {
        var channels = packet.channels();

        byteBuf.writeVarInt(channels.size());
        channels.forEach((namespace, version) -> {
            byteBuf.writeUtf(namespace);

            byteBuf.writeUtf(version.modVersion());
            byteBuf.writeUtf(version.protocolVersion());
            byteBuf.writeBoolean(version.requireRemote());
        });
    }

    private static C2SModListCheckPacket decodeFromBuf(FriendlyByteBuf byteBuf) {
        Map<String, FabricNetworkHelperImpl.ChannelVersion> map = new HashMap<>();

        int size = byteBuf.readVarInt();
        for (int i = 0; i < size; i++) {
            String namespace = byteBuf.readUtf();

            String modVersion = byteBuf.readUtf();
            String protocolVersion = byteBuf.readUtf();
            boolean requireRemote = byteBuf.readBoolean();
            map.put(namespace, new FabricNetworkHelperImpl.ChannelVersion(modVersion, protocolVersion, requireRemote));
        }

        return new C2SModListCheckPacket(map);
    }
}
