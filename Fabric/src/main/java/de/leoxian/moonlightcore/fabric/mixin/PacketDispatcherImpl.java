package de.leoxian.moonlightcore.fabric.mixin;

import de.leoxian.moonlightcore.api.network.MoonlightCustomPacket;
import de.leoxian.moonlightcore.api.network.PacketDispatcher;
import de.leoxian.moonlightcore.api.util.nullness.NotnullTriConsumer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@SuppressWarnings("all")
@Mixin(value = PacketDispatcher.class, remap = false)
public class PacketDispatcherImpl {

    @Overwrite
    protected <MSG extends MoonlightCustomPacket.ServerBoundCustomPacket<MSG>> void registerServerBound(ResourceLocation id, Class<MSG> type, MoonlightCustomPacket.PacketCodec<MSG> codec, NotnullTriConsumer<MinecraftServer, ServerPlayer, MSG> handler) {
        ServerPlayNetworking.registerGlobalReceiver(id, (server, player, $, buf, $1) -> {
            MSG packet = codec.decode(buf);

            server.execute(() -> handler.accept(server, player, packet));
        });
    }

    @Overwrite
    public <MSG extends MoonlightCustomPacket.ClientBoundCustomPacket<MSG>> void sendToPlayer(ServerPlayer target, MSG packet) {
        ResourceLocation id = packet.id();

        if(ServerPlayNetworking.canSend(target, id)) {
            ServerPlayNetworking.send(target, id, packet.toBuf());
        }
    }

}
