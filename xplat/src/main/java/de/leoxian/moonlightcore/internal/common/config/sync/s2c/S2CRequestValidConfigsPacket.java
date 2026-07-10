package de.leoxian.moonlightcore.internal.common.config.sync.s2c;

import de.leoxian.moonlightcore.client.network.ClientConfigurationNetworking;
import de.leoxian.moonlightcore.client.network.ClientPacketDistributor;
import de.leoxian.moonlightcore.client.network.ClientPlayNetworking;
import de.leoxian.moonlightcore.internal.common.config.ConfigRegistry;
import de.leoxian.moonlightcore.internal.common.config.sync.c2s.C2SAcceptedValidConfigs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public enum S2CRequestValidConfigsPacket implements CustomPacketPayload {
    INSTANCE
    ;
    public static final Type<S2CRequestValidConfigsPacket> TYPE = new Type<>(Identifier.parse("moonlightcore:request_valid_configs"));
    public static final StreamCodec<ByteBuf, S2CRequestValidConfigsPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static void handleConfiguration(S2CRequestValidConfigsPacket packet, ClientConfigurationNetworking.Context context) {
        context.minecraft().execute(() -> {
            if (ClientPlayNetworking.canSend(S2CRequestValidConfigsPacket.TYPE)) {
                ClientPacketDistributor.sendToServer(new C2SAcceptedValidConfigs(ConfigRegistry.getSyncableConfigs()));
            }
        });
    }

    public static void handlePlay(S2CRequestValidConfigsPacket packet, ClientPlayNetworking.Context context) {
        context.minecraft().execute(() -> {
            if (ClientPlayNetworking.canSend(S2CRequestValidConfigsPacket.TYPE)) {
                ClientPacketDistributor.sendToServer(new C2SAcceptedValidConfigs(ConfigRegistry.getSyncableConfigs()));
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
