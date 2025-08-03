package de.leowgc.moonlightcore.fabric.mixin.client;

import de.leowgc.moonlightcore.api.network.MoonlightCustomPacket;
import de.leowgc.moonlightcore.api.network.PacketDispatcher;
import de.leowgc.moonlightcore.api.util.nullness.NotnullConsumer;
import de.leowgc.moonlightcore.api.util.nullness.NotnullTriConsumer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@SuppressWarnings("all")
@Mixin(value = PacketDispatcher.class, remap = false)
public class PacketDispatcherImpl {

    @Overwrite
    protected <MSG extends MoonlightCustomPacket.ClientBoundCustomPacket<MSG>> void registerClientBound(ResourceLocation id, Class<MSG> type, MoonlightCustomPacket.PacketCodec<MSG> codec, NotnullConsumer<MSG> handler) {
        ClientPlayNetworking.registerGlobalReceiver(id, (client, $1, byteBuf, $2) -> {
            byteBuf.readByte();
            MSG packet = codec.decode(byteBuf);

            client.execute(() -> handler.accept(packet));
        });
    }

    @Overwrite
    public <MSG extends MoonlightCustomPacket.ServerBoundCustomPacket<MSG>> void sendToServer(MSG packet) {
        ResourceLocation id = packet.id();

        if(ClientPlayNetworking.canSend(id)) {
            FriendlyByteBuf byteBuf = PacketByteBufs.create();
            byteBuf.writeByte(0);
            packet.codec().encode(byteBuf, packet);

            ClientPlayNetworking.send(id, byteBuf);
        }
    }

    @Overwrite
    protected <MSG extends MoonlightCustomPacket.ServerBoundCustomPacket<MSG>> void registerServerBound(ResourceLocation id, Class<MSG> type, MoonlightCustomPacket.PacketCodec<MSG> codec, NotnullTriConsumer<MinecraftServer, ServerPlayer, MSG> handler) {}

    @Overwrite
    public <MSG extends MoonlightCustomPacket.ClientBoundCustomPacket<MSG>> void sendToPlayer(ServerPlayer target, MSG packet) {}

}
