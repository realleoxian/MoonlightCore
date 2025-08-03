package de.leowgc.moonlightcore.forge.mixin;

import de.leowgc.moonlightcore.api.network.MoonlightCustomPacket;
import de.leowgc.moonlightcore.api.network.PacketDispatcher;
import de.leowgc.moonlightcore.api.util.nullness.NotnullConsumer;
import de.leowgc.moonlightcore.api.util.nullness.NotnullTriConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.spongepowered.asm.mixin.*;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("all")
@Mixin(value = PacketDispatcher.class, remap = false)
public abstract class PacketDispatcherImpl {
    @Shadow @Final
    private ResourceLocation channelName;

    @Unique
    private SimpleChannel channel = null;
    @Unique
    private final AtomicInteger packetId = new AtomicInteger();

    @Overwrite
    protected <MSG extends MoonlightCustomPacket.ClientBoundCustomPacket<MSG>> void registerClientBound(ResourceLocation id, Class<MSG> type, MoonlightCustomPacket.PacketCodec<MSG> codec, NotnullConsumer<MSG> handler) {
        this.channel.messageBuilder(type, this.packetId.incrementAndGet(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder((msg, buf) -> codec.encode(buf, msg)).decoder(codec::decode)
                .consumerMainThread((msg, forgeCtx) -> forgeCtx.get().enqueueWork(() -> {
                    handler.accept(msg);
                    forgeCtx.get().setPacketHandled(true);
                })).add();
    }

    @Overwrite
    protected <MSG extends MoonlightCustomPacket.ServerBoundCustomPacket<MSG>> void registerServerBound(ResourceLocation id, Class<MSG> type, MoonlightCustomPacket.PacketCodec<MSG> codec, NotnullTriConsumer<MinecraftServer, ServerPlayer, MSG> handler) {
        this.channel.messageBuilder(type, this.packetId.incrementAndGet(), NetworkDirection.PLAY_TO_SERVER)
                .encoder((msg, buf) -> codec.encode(buf, msg)).decoder(codec::decode)
                .consumerMainThread((msg, forgeCtx) -> forgeCtx.get().enqueueWork(() -> {
                    ServerPlayer player = forgeCtx.get().getSender();

                    handler.accept(player.getServer(), player, msg);
                    forgeCtx.get().setPacketHandled(true);
                })).add();;
    }

    @Overwrite
    public <MSG extends MoonlightCustomPacket.ServerBoundCustomPacket<MSG>> void sendToServer(MSG packet) {
        this.channel.send(PacketDistributor.SERVER.noArg(), packet);
    }

    @Overwrite
    public <MSG extends MoonlightCustomPacket.ClientBoundCustomPacket<MSG>> void sendToPlayer(ServerPlayer target, MSG packet) {
        this.channel.send(PacketDistributor.PLAYER.with(() -> target), packet);
    }

    @Overwrite
    private void initForge() {
        String protocolVersion = Integer.toString(1);

        this.channel = NetworkRegistry.ChannelBuilder.named(this.channelName)
                .clientAcceptedVersions(protocolVersion::equals).serverAcceptedVersions(protocolVersion::equals)
                .networkProtocolVersion(() -> protocolVersion).simpleChannel();
    }
}
