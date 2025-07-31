package de.leoxian.moonlightcore.fabric.mixin;

import de.leoxian.moonlightcore.api.network.MoonlightCustomPacket;
import de.leoxian.moonlightcore.api.network.PacketDispatcher;
import de.leoxian.moonlightcore.api.util.nullness.NotnullConsumer;
import de.leoxian.moonlightcore.api.util.nullness.NotnullTriConsumer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.*;

@SuppressWarnings("all")
@Mixin(value = PacketDispatcher.class, remap = false, priority = 0)
public class PacketDispatcherImpl {

    @Overwrite
    protected <MSG extends MoonlightCustomPacket.ServerBoundCustomPacket<MSG>> void registerServerBound(ResourceLocation id, Class<MSG> type, MoonlightCustomPacket.PacketCodec<MSG> codec, NotnullTriConsumer<MinecraftServer, ServerPlayer, MSG> handler) {
        ServerPlayNetworking.registerGlobalReceiver(id, (server, player, $, buf, responseSender) -> {
            buf.readByte();
            MSG data = codec.decode(buf);

            server.execute(() -> handler.accept(server, player, data));
        });
    }

    @Overwrite
    public <MSG extends MoonlightCustomPacket.ClientBoundCustomPacket<MSG>> void sendToPlayer(ServerPlayer target, MSG packet) {
        ResourceLocation id = packet.id();

        if(ServerPlayNetworking.canSend(target, id)) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeByte(0);
            packet.codec().encode(buf, packet);

            ServerPlayNetworking.send(target, id, buf);
        }
    }

    @Overwrite
    protected <MSG extends MoonlightCustomPacket.ClientBoundCustomPacket<MSG>> void registerClientBound(ResourceLocation id, Class<MSG> type, MoonlightCustomPacket.PacketCodec<MSG> codec, NotnullConsumer<MSG> handler) {}

    @Overwrite
    public <MSG extends MoonlightCustomPacket.ServerBoundCustomPacket<MSG>> void sendToServer(MSG packet) {}

}
