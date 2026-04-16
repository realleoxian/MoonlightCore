package de.realleoxian.moonlightcore.forge.network;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.network.PacketType;
import de.realleoxian.moonlightcore.forge.client.network.ForgeClientPacketContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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

    private static final Map<Class<?>, PacketType<?>> CLIENT_PACKETS_BY_CLASS = new HashMap<>();
    private static final Map<Class<?>, PacketType<?>> SERVER_PACKETS_BY_CLASS = new HashMap<>();

    private static ForgeNetworkHelper INSTANCE = null;

    public static NetworkHelper get() {
        if (ForgeNetworkHelper.INSTANCE == null) ForgeNetworkHelper.INSTANCE = new ForgeNetworkHelper();
        return ForgeNetworkHelper.INSTANCE;
    }

    private final Map<String, ForgePacketRegistrar> registrars = new HashMap<>();

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
        PacketType<MSG> packetType = (PacketType<MSG>) SERVER_PACKETS_BY_CLASS.get(packet.getClass());
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
        PacketType<MSG> packetType = (PacketType<MSG>) CLIENT_PACKETS_BY_CLASS.get(packet.getClass());
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

    private static PacketContext createContext(EnvSide reception, NetworkEvent.Context forgeCtx) {
        return switch (reception) {
            case CLIENT -> ForgeClientPacketContext.INSTANCE;
            case SERVER -> new ForgeServerPacketContext(forgeCtx);
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
        public <MSG> void bidirectional(PacketType<MSG> type, BiConsumer<PacketContext, MSG> handler) {
            var builder = this.channel.messageBuilder(type.type(), packetId.incrementAndGet()).encoder((msg, buf) -> type.encoder().write(buf, msg)).decoder(type.decoder()::read);
            switch (this.handlerThread) {
                case MAIN -> builder = builder.consumerMainThread((packet, forgeCtxSup) -> {
                    NetworkEvent.Context forgeCtx = forgeCtxSup.get();

                    EnvSide receptionSide = forgeCtx.getDirection().getReceptionSide().isClient() ? EnvSide.CLIENT : EnvSide.SERVER;
                    forgeCtx.enqueueWork(() -> handler.accept(createContext(receptionSide, forgeCtx), packet));
                    forgeCtx.setPacketHandled(true);
                });
                case NETWORK -> builder = builder.consumerNetworkThread((packet, forgeCtxSup) -> {
                    NetworkEvent.Context forgeCtx = forgeCtxSup.get();

                    EnvSide receptionSide = forgeCtx.getDirection().getReceptionSide().isClient() ? EnvSide.CLIENT : EnvSide.SERVER;
                    handler.accept(createContext(receptionSide, forgeCtx), packet);
                    forgeCtx.setPacketHandled(true);
                });
            }

            builder.add();
            CLIENT_PACKETS_BY_CLASS.put(type.type(), type);
            SERVER_PACKETS_BY_CLASS.put(type.type(), type);
        }

        @Override
        public <MSG> void clientbound(PacketType<MSG> type, BiConsumer<PacketContext, MSG> handler) {
            var builder = this.channel.messageBuilder(type.type(), packetId.incrementAndGet(), NetworkDirection.PLAY_TO_CLIENT).encoder((msg, buf) -> type.encoder().write(buf, msg)).decoder(type.decoder()::read);
            switch (this.handlerThread) {
                case MAIN -> builder = builder.consumerMainThread((packet, forgeCtxSup) -> {
                    NetworkEvent.Context forgeCtx = forgeCtxSup.get();

                    forgeCtx.enqueueWork(() -> handler.accept(ForgeClientPacketContext.INSTANCE, packet));
                    forgeCtx.setPacketHandled(true);
                });
                case NETWORK -> builder = builder.consumerNetworkThread((packet, forgeCtxSup) -> {
                    NetworkEvent.Context forgeCtx = forgeCtxSup.get();

                    handler.accept(ForgeClientPacketContext.INSTANCE, packet);
                    forgeCtx.setPacketHandled(true);
                });
            }

            builder.add();
            CLIENT_PACKETS_BY_CLASS.put(type.type(), type);
        }

        @Override
        public <MSG> void serverbound(PacketType<MSG> type, BiConsumer<PacketContext, MSG> handler) {
            var builder = this.channel.messageBuilder(type.type(), packetId.incrementAndGet(), NetworkDirection.PLAY_TO_SERVER).encoder((msg, buf) -> type.encoder().write(buf, msg)).decoder(type.decoder()::read);
            switch (this.handlerThread) {
                case MAIN -> builder = builder.consumerMainThread((packet, forgeCtxSup) -> {
                    NetworkEvent.Context forgeCtx = forgeCtxSup.get();

                    forgeCtx.enqueueWork(() -> handler.accept(new ForgeServerPacketContext(forgeCtx), packet));
                    forgeCtx.setPacketHandled(true);
                });
                case NETWORK -> builder = builder.consumerNetworkThread((packet, forgeCtxSup) -> {
                    NetworkEvent.Context forgeCtx = forgeCtxSup.get();

                    handler.accept(new ForgeServerPacketContext(forgeCtx), packet);
                    forgeCtx.setPacketHandled(true);
                });
            }

            builder.add();
            SERVER_PACKETS_BY_CLASS.put(type.type(), type);
        }
    }
}
