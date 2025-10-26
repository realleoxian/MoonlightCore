package de.leoxian.moonlightcore.fabric.mixin.impl;

import de.leoxian.moonlightcore.network.CustomPacket;
import de.leoxian.moonlightcore.network.PacketDispatcher;
import de.leoxian.moonlightcore.util.StreamCodec;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.util.TriConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = PacketDispatcher.class, remap = false)
public class PacketDispatcherImplMixin {

     @Overwrite
     protected <MSG extends CustomPacket<MSG>> void registerServerPacket(ResourceLocation id, Class<MSG> packetType, StreamCodec<? super FriendlyByteBuf, MSG> codec, TriConsumer<MinecraftServer, ServerPlayer, MSG> handler) {
          ServerPlayNetworking.registerGlobalReceiver(id, (server, player, $, buf, $1) -> {
               MSG packet = codec.decode(buf);
               buf.readByte();

               server.execute(() -> handler.accept(server, player, packet));
          });
     }

     @Overwrite
     public <MSG extends CustomPacket<MSG>> void sendToPlayer(ServerPlayer player, MSG packet) {
          ResourceLocation packetId = packet.id();

          if(ServerPlayNetworking.canSend(player, packetId)) {
               FriendlyByteBuf buf = PacketByteBufs.create();

               buf.writeByte(0);
               packet.codec().encode(buf, packet);

               ServerPlayNetworking.send(player, packetId, buf);
          }
     }

}
