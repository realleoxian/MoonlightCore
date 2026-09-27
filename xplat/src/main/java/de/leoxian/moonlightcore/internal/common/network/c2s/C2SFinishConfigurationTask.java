package de.leoxian.moonlightcore.internal.common.network.c2s;

import de.leoxian.moonlightcore.common.network.ServerConfigurationNetworking;
import de.leoxian.moonlightcore.internal.common.network.task.SyncConfigurationTask;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

// This packet its only used at configuration to mark the sync task as finished
public enum C2SFinishConfigurationTask implements CustomPacketPayload {
    INSTANCE
    ;
    public static final Type<C2SFinishConfigurationTask> TYPE = new Type<>(Identifier.fromNamespaceAndPath("moonlightcore", "finish_configuration_task"));
    public static final StreamCodec<? super FriendlyByteBuf, C2SFinishConfigurationTask> STREAM_CODEC = StreamCodec.unit(C2SFinishConfigurationTask.INSTANCE);

    public static void handleConfiguration(C2SFinishConfigurationTask task, ServerConfigurationNetworking.Context context) {
        context.enqueueWork(() -> context.completeTask(SyncConfigurationTask.TYPE));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
