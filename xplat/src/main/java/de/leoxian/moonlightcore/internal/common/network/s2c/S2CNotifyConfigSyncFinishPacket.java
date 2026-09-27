package de.leoxian.moonlightcore.internal.common.network.s2c;

import de.leoxian.moonlightcore.client.network.ClientConfigurationNetworking;
import de.leoxian.moonlightcore.internal.common.network.c2s.C2SFinishConfigurationTask;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public enum S2CNotifyConfigSyncFinishPacket implements CustomPacketPayload {
    INSTANCE
    ;
    public static final Type<S2CNotifyConfigSyncFinishPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath("moonlightcore", "notify_config_sync_finish"));
    public static final StreamCodec<FriendlyByteBuf, S2CNotifyConfigSyncFinishPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static void handle(S2CNotifyConfigSyncFinishPacket packet, ClientConfigurationNetworking.Context context) {
        context.enqueueWork(() -> {
           context.responseSender().sendPacket(C2SFinishConfigurationTask.INSTANCE);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
