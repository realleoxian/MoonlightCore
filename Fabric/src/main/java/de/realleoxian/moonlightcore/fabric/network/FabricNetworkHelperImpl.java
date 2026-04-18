package de.realleoxian.moonlightcore.fabric.network;

import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.network.PacketType;
import de.realleoxian.moonlightcore.fabric.client.network.FabricClientPacketContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.BiConsumer;

public class FabricNetworkHelperImpl implements NetworkHelper {
    public record ChannelVersion(String modVersion, String protocolVersion, boolean requireRemote) {}

    private final Map<String, FabricPacketRegistrar> registrars = new HashMap<>();

    private final Map<Class<?>, PacketType<?>> clientPacketsByClass = new HashMap<>();
    private final Map<Class<?>, PacketType<?>> serverPacketsByClass = new HashMap<>();

    private final Set<String> registeredMods = new HashSet<>();
    private final Map<String, String> channelsProtocolVersions = new HashMap<>();
    private final Map<String, HandlerThread> channelHandlerThreads = new HashMap<>();
    private final Set<String> clientOnlyChannels = new HashSet<>();
    private final Set<String> serverOnlyChannels = new HashSet<>();

    @Override
    public NetworkHelper clientOnly(String namespace) {
        this.clientOnlyChannels.add(namespace);
        return this;
    }

    @Override
    public NetworkHelper serverOnly(String namespace) {
        this.serverOnlyChannels.add(namespace);
        return this;
    }

    @Override
    public NetworkHelper protocolVersion(String namespace, String protocolVersion) {
        try {
            int val = Integer.parseInt(protocolVersion);
            this.channelsProtocolVersions.put(namespace, Integer.toString(val));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Network protocol version must be numeric for forge compat");
        }

        return this;
    }

    @Override
    public NetworkHelper handlerThread(String namespace, HandlerThread handlerThread) {
        this.channelHandlerThreads.put(namespace, handlerThread);
        return this;
    }

    @Override
    public PacketRegistrar registrar(String namespace) {
        return this.registrars.computeIfAbsent(namespace, FabricPacketRegistrar::new);
    }

    @Override
    public <MSG> void sendToServer(MSG packet) {
        @SuppressWarnings("unchecked")
        PacketType<MSG> type = (PacketType<MSG>) serverPacketsByClass.get(packet.getClass());
        if (type == null) {
            throw new IllegalArgumentException("C2S packet not registered: " + packet.getClass().getSimpleName());
        }

        ResourceLocation name = type.name();
        if (canServerReceive(name)) {
            ClientPlayNetworking.send(name, type.encode(packet));
        }
    }

    @Override
    public <MSG> void sendToPlayer(ServerPlayer player, MSG packet) {
        @SuppressWarnings("unchecked")
        PacketType<MSG> type = (PacketType<MSG>) clientPacketsByClass.get(packet.getClass());
        if (type == null) {
            throw new IllegalArgumentException("S2C packet not registered: " + packet.getClass().getSimpleName());
        }

        ResourceLocation name = type.name();
        if (canPlayerReceive(player, name)) {
            ServerPlayNetworking.send(player, name, type.encode(packet));
        }
    }

    @Override
    public boolean canServerReceive(ResourceLocation packet) {
        return ClientPlayNetworking.canSend(packet);
    }

    @Override
    public boolean canPlayerReceive(ServerPlayer player, ResourceLocation packet) {
        return ServerPlayNetworking.canSend(player, packet);
    }

    @UnmodifiableView
    public Collection<String> getRegisteredMods() {
        return Collections.unmodifiableCollection(this.registeredMods);
    }

    public Optional<ChannelVersion> getChannelVersion(String namespace, EnvSide side) {
        FabricPacketRegistrar registrar = this.registrars.get(namespace);
        if (registrar == null) {
            throw new IllegalArgumentException("Cannot get channel version of mod '" + namespace + "'");
        }

        return FabricLoader.getInstance().getModContainer(namespace)
                .map(ModContainer::getMetadata).map(m -> m.getVersion().getFriendlyString())
                .map(modVersion -> {
                    String channelVersion = channelsProtocolVersions.getOrDefault(namespace, "1");
                    return new ChannelVersion(modVersion, channelVersion, side == EnvSide.CLIENT ? !clientOnlyChannels.contains(namespace) : !serverOnlyChannels.contains(namespace));
                });
    }

    private class FabricPacketRegistrar implements PacketRegistrar {
        private final HandlerThread handlerThread;

        private FabricPacketRegistrar(String namespace) {
            registeredMods.add(namespace);
            this.handlerThread = channelHandlerThreads.getOrDefault(namespace, HandlerThread.NETWORK);
        }

        @Override
        public <MSG> void clientbound(PacketType<MSG> type, BiConsumer<MSG, PacketContext<ClientPacketListener>> handler) {
            if (EnvSide.CLIENT.isCurrent()) {
                ClientPlayNetworking.registerGlobalReceiver(type.name(), (client, packetListener, buf, responseSender) -> {
                    MSG packet = type.decoder().read(buf);

                    switch (this.handlerThread) {
                        case NETWORK -> handler.accept(packet, FabricClientPacketContext.INSTANCE);
                        case MAIN -> client.execute(() -> handler.accept(packet, FabricClientPacketContext.INSTANCE));
                    }
                });
                clientPacketsByClass.put(type.type(), type);
            }
        }

        @Override
        public <MSG> void serverbound(PacketType<MSG> type, BiConsumer<MSG, PacketContext<ServerGamePacketListenerImpl>> handler) {
            ServerPlayNetworking.registerGlobalReceiver(type.name(), (server, player, packetListener, buf, responseSender) -> {
                PacketContext<ServerGamePacketListenerImpl> context = new FabricServerPacketContext(server, player, packetListener);
                MSG packet = type.decoder().read(buf);

                switch (this.handlerThread) {
                    case NETWORK -> handler.accept(packet, context);
                    case MAIN -> server.execute(() -> handler.accept(packet, context));
                }
            });

            serverPacketsByClass.put(type.type(), type);
        }
    }
}
