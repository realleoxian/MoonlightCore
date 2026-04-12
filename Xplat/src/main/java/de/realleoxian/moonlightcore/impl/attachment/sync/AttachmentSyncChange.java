package de.realleoxian.moonlightcore.impl.attachment.sync;

import com.mojang.logging.LogUtils;
import de.realleoxian.moonlightcore.api.attachment.AttachmentHolder;
import de.realleoxian.moonlightcore.api.attachment.AttachmentsHolderInfo;
import de.realleoxian.moonlightcore.api.attachment.AttachmentType;
import de.realleoxian.moonlightcore.impl.attachment.AttachmentTypeImpl;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import de.realleoxian.moonlightcore.mixin.ServerboundCustomPayloadPacketAccessor;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import java.util.Objects;

public record AttachmentSyncChange(AttachmentsHolderInfo<?, ?> holderInfo, AttachmentType<?> type, byte[] data) {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_PADDING_SIZE_IN_BYTES = AttachmentsHolderInfo.MAX_SIZE_IN_BYTES + AttachmentTypeImpl.MAX_SYNCED_NAME_SIZE;
    private static final int MAX_DATA_IN_BYTES = ServerboundCustomPayloadPacketAccessor.getMAX_PAYLOAD_SIZE() - MAX_PADDING_SIZE_IN_BYTES;

    @SuppressWarnings("unchecked")
    public static AttachmentSyncChange create(AttachmentsHolderInfo<?, ?> holderInfo, AttachmentType<?> type, @Nullable Object value) {
        Objects.requireNonNull(holderInfo, "AttachmentHolder cannot be 'null'");
        if(!type.isSync()) {
            throw new IllegalArgumentException("Cannot create sync change for non-syncable attachment type");
        }

        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        if(value == null) {
            byteBuf.writeBoolean(false);
        } else {
            byteBuf.writeBoolean(true);
            ((AttachmentType<Object>) type).syncHandler().encode(byteBuf, (AttachmentType<Object>) type, value);
        }

        byte[] encoded = byteBuf.array();
        if(encoded.length > MAX_DATA_IN_BYTES) {
            throw new EncoderException("Encoded data too long. Attachment sync changes can only encode a maximum of " + MAX_DATA_IN_BYTES + " bytes");
        }

        return new AttachmentSyncChange(holderInfo, type, encoded);
    }

    @SuppressWarnings("unchecked")
    public void applyChange(Level level) {
        Objects.requireNonNull(level, "Level cannot be 'null'");

        Object holder = holderInfo.get(level);
        if(holder == null) {
            LOGGER.warn("Couldn't find the attachments holder in server-side level, not applying changes");
            return;
        }

        if (!(holder instanceof AttachmentHolder attachmentHolder)) {
            throw new IllegalStateException("The attachments holder doesn't actually implements the AttachmentHolder interface");
        }

        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.copiedBuffer(data()));
        Object value = byteBuf.readBoolean() ? ((AttachmentType<Object>) type).syncHandler().decode(byteBuf, (AttachmentType<Object>) type) : null;
        attachmentHolder.getAttachmentsMap().set((AttachmentType<Object>) type, value);
    }
}
