package de.realleoxian.moonlightcore.forge.network;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.network.PacketType;
import de.realleoxian.moonlightcore.forge.client.network.ForgeClientPacketContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public final class ForgeNetworkHelper implements NetworkHelper {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ForgeNetworkHelper INSTANCE = null;

    public static NetworkHelper get() {
        if (ForgeNetworkHelper.INSTANCE == null) ForgeNetworkHelper.INSTANCE = new ForgeNetworkHelper();
        return ForgeNetworkHelper.INSTANCE;
    }

    private final Map<String, ForgePacketRegistrar> registrars = new HashMap<>();

    private final Map<Class<?>, PacketType<?>> clientPacketsByClass = new HashMap<>();
    private final Map<Class<?>, PacketType<?>> serverPacketsByClass = new HashMap<>();

    private final Map<String, Integer> protocolVersions = Maps.newConcurrentMap();
    private final Map<String, HandlerThread> handlerThreads = Maps.newConcurrentMap();
    private final Map<String, Predicate<String>> clientOnlyChannels = Maps.newConcurrentMap();
    private final Map<String, Predicate<String>> serverOnlyChannels = Maps.newConcurrentMap();

    @Override
    public PacketRegistrar registrar(String namespace) {
        return this.registrars.computeIfAbsent(namespace, ForgePacketRegistrar::new);
    }

    @Override
    public NetworkHelper clientOnly(String namespace) {
        this.clientOnlyChannels.put(namespace, $ -> true);
        return this;
    }

    @Override
    public NetworkHelper serverOnly(String namespace) {
        this.serverOnlyChannels.put(namespace, $ -> true);
        return this;
    }

    @Override
    public NetworkHelper handlerThread(String namespace, HandlerThread handlerThread) {
        this.handlerThreads.put(namespace, handlerThread);
        return this;
    }

    @Override
    public <MSG> void sendToServer(MSG packet) {
        @SuppressWarnings("unchecked")
        PacketType<MSG> packetType = (PacketType<MSG>) serverPacketsByClass.get(packet.getClass());
        if (packetType == null) {
            throw new IllegalArgumentException("C2S packet not registered: '" + packet.getClass().getSimpleName() + "'");
        }

        ResourceLocation name = packetType.name();
        if (this.canServerReceive(name)) {
            this.getChannel(name.getNamespace()).sendToServer(packet);
        }
    }

    @Override
    public <MSG> void sendToPlayer(ServerPlayer player, MSG packet) {
        @SuppressWarnings("unchecked")
        PacketType<MSG> packetType = (PacketType<MSG>) clientPacketsByClass.get(packet.getClass());
        if (packetType == null) {
            throw new IllegalArgumentException("S2C packet not registered: '" + packet.getClass().getSimpleName() + "'");
        }

        ResourceLocation name = packetType.name();
        if (this.canPlayerReceive(player, name)) {
            this.getChannel(name.getNamespace()).send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    @Override
    public NetworkHelper protocolVersion(String namespace, String protocolVersion) {
        try {
            int val = Integer.parseInt(protocolVersion);
            this.protocolVersions.put(namespace, val);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Network protocol version must be numeric for forge compat");
        }
        return this;
    }

    @Override
    public boolean canServerReceive(ResourceLocation packet) {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        if (listener == null) {
            LOGGER.debug("Cannot check if server can receive {} packet, client isn't connected to any server", packet);
            return false;
        }

        return getChannel(packet.getNamespace()).isRemotePresent(listener.getConnection());
    }

    @Override
    public boolean canPlayerReceive(ServerPlayer player, ResourceLocation packet) {
        return getChannel(packet.getNamespace()).isRemotePresent(player.connection.connection);
    }

    private SimpleChannel getChannel(String namespace) {
        ForgePacketRegistrar registrar = this.registrars.get(namespace);
        if (registrar == null)
            throw new IllegalArgumentException("No packet registrar made for namespace '" + namespace + "'");

        return registrar.channel;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static PacketContext<PacketListener> createContext(EnvSide reception, NetworkEvent.Context forgeCtx) {
        return switch (reception) {
            case CLIENT -> (PacketContext<PacketListener>) (PacketContext) ForgeClientPacketContext.INSTANCE;
            case SERVER -> {
                ServerPlayer player = Objects.requireNonNull(forgeCtx.getSender(), "This shouldn't happen, but server player it's being null-");
                MinecraftServer server = player.server;

                yield (PacketContext<PacketListener>) (PacketContext) new ForgeServerPacketContext(player, server);
            }
        };
    }

    private static boolean defaultVersionCheck(String str) {
        return str.equals("1");
    }

    private ForgeNetworkHelper() {}

    private final class ForgePacketRegistrar implements PacketRegistrar {
        private final SimpleChannel channel;
        private final HandlerThread handlerThread;
        private final AtomicInteger packetId = new AtomicInteger(-1);

        private ForgePacketRegistrar(String namespace) {
            this.channel = NetworkRegistry.ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(namespace, "network"))
                    .clientAcceptedVersions(s -> clientOnlyChannels.getOrDefault(namespace, ForgeNetworkHelper::defaultVersionCheck).test(s))
                    .serverAcceptedVersions(s -> serverOnlyChannels.getOrDefault(namespace, ForgeNetworkHelper::defaultVersionCheck).test(s))
                    .networkProtocolVersion(() -> String.valueOf(protocolVersions.getOrDefault(namespace, 1))).simpleChannel();
            this.handlerThread = handlerThreads.getOrDefault(namespace, HandlerThread.NETWORK);
        }

        @Override
        public <MSG> void clientbound(PacketType<MSG> type, BiConsumer<MSG, PacketContext<ClientPacketListener>> handler) {
            var builder = this.channel.messageBuilder(type.type(), packetId.incrementAndGet(), NetworkDirection.PLAY_TO_CLIENT).encoder(type.encoder()::write).decoder(type.decoder()::read);
            switch (this.handlerThread) {
                case MAIN -> builder = builder.consumerMainThread((packet, forgeCtxSup) -> {
                    NetworkEvent.Context forgeCtx = forgeCtxSup.get();

                    forgeCtx.enqueueWork(() -> handler.accept(packet, ForgeClientPacketContext.INSTANCE));
                    forgeCtx.setPacketHandled(true);
                });
                case NETWORK -> builder = builder.consumerNetworkThread((packet, forgeCtxSup) -> {
                    NetworkEvent.Context forgeCtx = forgeCtxSup.get();

                    handler.accept(packet, ForgeClientPacketContext.INSTANCE);
                    forgeCtx.setPacketHandled(true);
                });
            }

            builder.add();
            clientPacketsByClass.put(type.type(), type);
        }

        @Override
        public <MSG> void serverbound(PacketType<MSG> type, BiConsumer<MSG, PacketContext<ServerGamePacketListenerImpl>> handler) {
            var builder = this.channel.messageBuilder(type.type(), packetId.incrementAndGet(), NetworkDirection.PLAY_TO_SERVER).encoder(type.encoder()::write).decoder(type.decoder()::read);
            switch (this.handlerThread) {
                case MAIN -> builder = builder.consumerMainThread((packet, forgeCtxSup) -> {
                    NetworkEvent.Context forgeCtx = forgeCtxSup.get();
                    ServerPlayer player = Objects.requireNonNull(forgeCtx.getSender(), "This shouldn't happen, but server player it's being null-");

                    forgeCtx.enqueueWork(() -> handler.accept(packet, new ForgeServerPacketContext(player, player.server)));
                    forgeCtx.setPacketHandled(true);
                });
                case NETWORK -> builder = builder.consumerNetworkThread((packet, forgeCtxSup) -> {
                    NetworkEvent.Context forgeCtx = forgeCtxSup.get();
                    ServerPlayer player = Objects.requireNonNull(forgeCtx.getSender(), "This shouldn't happen, but server player it's being null-");

                    handler.accept(packet, new ForgeServerPacketContext(player, player.server));
                    forgeCtx.setPacketHandled(true);
                });
            }

            builder.add();
            serverPacketsByClass.put(type.type(), type);
        }
    }
}
