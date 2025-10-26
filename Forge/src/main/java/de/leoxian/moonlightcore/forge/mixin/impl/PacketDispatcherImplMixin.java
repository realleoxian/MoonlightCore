package de.leoxian.moonlightcore.forge.mixin.impl;

import de.leoxian.moonlightcore.network.CustomPacket;
import de.leoxian.moonlightcore.network.PacketDispatcher;
import de.leoxian.moonlightcore.util.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Mixin(value = PacketDispatcher.class, remap = false)
public class PacketDispatcherImplMixin {
     @Shadow @Final
     protected ResourceLocation channelName;

     @Unique
     private final AtomicInteger packetId = new AtomicInteger();
     @Unique @Nullable
     private SimpleChannel forgeChannel = null;

     @Overwrite
     protected <MSG extends CustomPacket<MSG>> void registerClientPacket(ResourceLocation id, Class<MSG> typeClass, StreamCodec<? super FriendlyByteBuf, MSG> codec, Consumer<MSG> handler) {
          this.forgeChannel.messageBuilder(typeClass, this.packetId.incrementAndGet())
                  .encoder((msg, buf) -> codec.encode(buf, msg)).decoder(codec::decode)
                  .consumerMainThread((msg, forgeCtxSupplier) -> {
                       handler.accept(msg);
                       forgeCtxSupplier.get().setPacketHandled(true);
                  }).add();
     }

     @Overwrite
     protected <MSG extends CustomPacket<MSG>> void registerServerPacket(ResourceLocation id, Class<MSG> packetType, StreamCodec<? super FriendlyByteBuf, MSG> codec, TriConsumer<MinecraftServer, ServerPlayer, MSG> handler) {
          this.forgeChannel.messageBuilder(packetType, this.packetId.incrementAndGet())
                  .encoder((msg, buf) -> codec.encode(buf, msg)).decoder(codec::decode)
                  .consumerMainThread((msg, forgeCtxSupplier) -> {
                       ServerPlayer player = forgeCtxSupplier.get().getSender();
                       MinecraftServer server = forgeCtxSupplier.get().getSender().server;

                       handler.accept(server, player, msg);
                       forgeCtxSupplier.get().setPacketHandled(true);
                  }).add();
     }

     @Overwrite
     public <MSG extends CustomPacket<MSG>> void sendToServer(MSG packet) {
          this.forgeChannel.send(PacketDistributor.SERVER.noArg(), packet);
     }

     @Overwrite
     public <MSG extends CustomPacket<MSG>> void sendToPlayer(ServerPlayer player, MSG packet) {
          this.forgeChannel.send(PacketDistributor.PLAYER.with(() -> player), packet);
     }

     @Overwrite
     private void initForge() {
          String protocolVersion = String.valueOf(1);

          this.forgeChannel = NetworkRegistry.ChannelBuilder.named(this.channelName)
                  .clientAcceptedVersions(protocolVersion::equals)
                  .serverAcceptedVersions(protocolVersion::equals)
                  .networkProtocolVersion(() -> protocolVersion).simpleChannel();
     }
}
