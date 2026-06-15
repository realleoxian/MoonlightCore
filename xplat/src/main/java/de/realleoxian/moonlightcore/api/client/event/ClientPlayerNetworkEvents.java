package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.Event;
import de.realleoxian.moonlightcore.api.event.EventBase;
import de.realleoxian.moonlightcore.api.network.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.jetbrains.annotations.ApiStatus;

public final class ClientPlayerNetworkEvents {
    public static final Event<LoggedIn> LOGGED_IN = Event.create(LoggedIn.class);
    public static final Event<LoggedOut> LOGGED_OUT = Event.create(LoggedOut.class);
    public static final Event<ConfigurationCompleted> CONFIGURATION_COMPLETED = Event.create(ConfigurationCompleted.class);

    private ClientPlayerNetworkEvents() {}

    public static final class LoggedIn extends EventBase {
        public final ClientPacketListener networkHandler;
        public final PacketSender packetSender;
        public final Minecraft minecraft;

        @ApiStatus.Internal
        public LoggedIn(ClientPacketListener networkHandler, PacketSender packetSender, Minecraft minecraft) {
            this.networkHandler = networkHandler;
            this.packetSender = packetSender;
            this.minecraft = minecraft;
        }
    }

    public static final class LoggedOut extends EventBase {
        public final ClientPacketListener networkHandler;
        public final Minecraft minecraft;

        @ApiStatus.Internal
        public LoggedOut(ClientPacketListener networkHandler, Minecraft minecraft) {
            this.networkHandler = networkHandler;
            this.minecraft = minecraft;
        }
    }

    public static final class ConfigurationCompleted extends EventBase {
        public final ClientConfigurationPacketListenerImpl networkHandler;
        public final Minecraft minecraft;

        public ConfigurationCompleted(ClientConfigurationPacketListenerImpl networkHandler, Minecraft minecraft) {
            this.networkHandler = networkHandler;
            this.minecraft = minecraft;
        }
    }
}
