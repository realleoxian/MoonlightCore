package de.leoxian.moonlightcore.network;

import de.leoxian.moonlightcore.util.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.function.Consumer;

public abstract class PacketDispatcher {

     protected final ResourceLocation channelName;

     protected PacketDispatcher(ResourceLocation channelName) {
          this.channelName = channelName;

          this.initForge();
          this.bootstrap();
     }

     protected abstract void bootstrap();

     protected <MSG extends CustomPacket<MSG>> void registerClientPacket(ResourceLocation id, Class<MSG> typeClass, StreamCodec<? super FriendlyByteBuf, MSG> codec, Consumer<MSG> handler) {}

     protected <MSG extends CustomPacket<MSG>> void registerServerPacket(ResourceLocation id, Class<MSG> packetType, StreamCodec<? super FriendlyByteBuf, MSG> codec, TriConsumer<MinecraftServer, ServerPlayer, MSG> handler) {}

     public <MSG extends CustomPacket<MSG>> void sendToServer(MSG packet) {}

     public <MSG extends CustomPacket<MSG>> void sendToPlayer(ServerPlayer player, MSG packet) {}

     public <MSG extends CustomPacket<MSG>> void sendToPlayers(Collection<ServerPlayer> players, MSG packet) {
          players.forEach(player -> this.sendToPlayer(player, packet));
     }

     public <MSG extends CustomPacket<MSG>> void sendToPlayersInLevel(ServerLevel level, MSG packet) {
          this.sendToPlayers(level.players(), packet);
     }

     public <MSG extends CustomPacket<MSG>> void sendToPlayersInServer(MinecraftServer server, MSG packet) {
          this.sendToPlayers(server.getPlayerList().getPlayers(), packet);
     }

     @ApiStatus.Internal
     private void initForge() {}

}
