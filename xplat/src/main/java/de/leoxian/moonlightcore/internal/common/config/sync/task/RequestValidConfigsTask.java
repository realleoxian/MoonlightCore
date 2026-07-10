package de.leoxian.moonlightcore.internal.common.config.sync.task;

import de.leoxian.moonlightcore.internal.common.config.sync.s2c.S2CRequestValidConfigsPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

import java.util.function.Consumer;

public record RequestValidConfigsTask(ServerConfigurationPacketListenerImpl packetListener) implements ConfigurationTask {
    public static final Type TYPE = new Type("moonlightcore:request_valid_configs");

    @Override
    public void start(Consumer<Packet<?>> connection) {
        connection.accept(new ClientboundCustomPayloadPacket(S2CRequestValidConfigsPacket.INSTANCE));
    }

    @Override
    public Type type() {
        return TYPE;
    }
}
