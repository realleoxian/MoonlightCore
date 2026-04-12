package de.realleoxian.moonlightcore.api.attachment;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

    public interface AttachmentSyncHandler<T> {

        void encode(FriendlyByteBuf byteBuf, AttachmentType<T> type, T data);

        T decode(FriendlyByteBuf byteBuf, AttachmentType<T> type);

        default boolean canSync(AttachmentHolder holder, ServerPlayer player) {
            return true;
        }

    }
