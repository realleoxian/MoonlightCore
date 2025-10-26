package de.leoxian.moonlightcore.fabric.mixin.impl.client;

import de.leoxian.moonlightcore.network.CustomPacket;
import de.leoxian.moonlightcore.network.PacketDispatcher;
import de.leoxian.moonlightcore.util.StreamCodec;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.function.Consumer;

@Mixin(value = PacketDispatcher.class, remap = false)
public class PacketDispatcherImplMixin {

     @Overwrite
     protected <MSG extends CustomPacket<MSG>> void registerClientPacket(ResourceLocation id, Class<MSG> typeClass, StreamCodec<? super FriendlyByteBuf, MSG> codec, Consumer<MSG> handler) {
          ClientPlayNetworking.registerGlobalReceiver(id, (client, $, buf, $1) -> {
               MSG packet = codec.decode(buf);

               buf.readByte();
               client.execute(() -> handler.accept(packet));
          });
     }

     @Overwrite
     public <MSG extends CustomPacket<MSG>> void sendToServer(MSG packet) {
          ResourceLocation packetId = packet.id();

          if(ClientPlayNetworking.canSend(packetId)) {
              FriendlyByteBuf buf = PacketByteBufs.create();

              buf.writeByte(0);
              packet.codec().encode(buf, packet);

              ClientPlayNetworking.send(packetId, buf);
          }
     }

}
