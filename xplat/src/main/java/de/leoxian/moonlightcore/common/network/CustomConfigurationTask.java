package de.leoxian.moonlightcore.common.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

public interface CustomConfigurationTask extends ConfigurationTask {
    void run(Consumer<CustomPacketPayload> output);

    @Override
    @ApiStatus.Internal
    @ApiStatus.NonExtendable
    default void start(Consumer<Packet<?>> output) {
        run((payload) -> output.accept(new ClientboundCustomPayloadPacket(payload)));
    }
}
