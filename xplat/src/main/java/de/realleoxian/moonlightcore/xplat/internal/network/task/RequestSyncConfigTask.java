package de.realleoxian.moonlightcore.xplat.internal.network.task;

import de.realleoxian.moonlightcore.api.network.ServerNetworking;
import de.realleoxian.moonlightcore.xplat.internal.network.clientbound.S2CRequestAcceptedModConfigsPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;

import java.util.function.Consumer;

public record RequestSyncConfigTask() implements ConfigurationTask {
    public static final Type TYPE = new Type("moonlightcore:request_sync_config");

    @Override
    public void start(Consumer<Packet<?>> consumer) {
        consumer.accept(ServerNetworking.createS2CPacket(S2CRequestAcceptedModConfigsPacket.INSTANCE));
    }

    @Override
    public Type type() {
        return TYPE;
    }
}
