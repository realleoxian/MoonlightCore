package de.leoxian.moonlightcore.api.network;

import de.leoxian.moonlightcore.api.util.ExpectPlatform;
import de.leoxian.moonlightcore.api.util.SidedEnvironment;
import de.leoxian.moonlightcore.api.util.nullness.NotnullConsumer;
import de.leoxian.moonlightcore.api.util.nullness.NotnullTriConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * This is a base class for other mods packet dispatcher, registering the packets automatically when creating an instance of the class that is extending this.
 * @since 4.0.0
 * @author Leoxian
 */
public abstract class PacketDispatcher {
    @SuppressWarnings("FieldCanBeLocal")
    private final ResourceLocation channelName;

    protected PacketDispatcher(ResourceLocation channelName) {
        this.channelName = channelName;

        this.initForge();
        this.bootstrap();
    }

    public abstract void bootstrap();

    @ExpectPlatform
    @SidedEnvironment(SidedEnvironment.Environment.CLIENT)
    protected <MSG extends MoonlightCustomPacket.ClientBoundCustomPacket<MSG>> void registerClientBound(ResourceLocation id, Class<MSG> type, MoonlightCustomPacket.PacketCodec<MSG> codec, NotnullConsumer<MSG> handler) {
        throw new UnsupportedOperationException();
    }

    @ExpectPlatform
    @SidedEnvironment(SidedEnvironment.Environment.SERVER)
    protected <MSG extends MoonlightCustomPacket.ServerBoundCustomPacket<MSG>> void registerServerBound(ResourceLocation id, Class<MSG> type, MoonlightCustomPacket.PacketCodec<MSG> codec, NotnullTriConsumer<MinecraftServer, ServerPlayer, MSG> handler) {
        throw new UnsupportedOperationException();
    }

    @ExpectPlatform
    @SidedEnvironment(SidedEnvironment.Environment.CLIENT)
    public <MSG extends MoonlightCustomPacket.ServerBoundCustomPacket<MSG>> void sendToServer(MSG packet) {
        throw new UnsupportedOperationException();
    }

    @ExpectPlatform
    @SidedEnvironment(SidedEnvironment.Environment.SERVER)
    public <MSG extends MoonlightCustomPacket.ClientBoundCustomPacket<MSG>> void sendToPlayer(ServerPlayer target, MSG packet) {
        throw new UnsupportedOperationException();
    }

    @ExpectPlatform(value = ExpectPlatform.Platform.FORGE)
    private void initForge() {}
}
