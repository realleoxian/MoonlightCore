package de.leoxian.moonlightcore.attachment.sync;

import de.leoxian.moonlightcore.attachment.AttachmentHolder;
import de.leoxian.moonlightcore.attachment.AttachmentType;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.core.network.clientbound.S2CAttachmentSyncPacket;
import de.leoxian.moonlightcore.mixin.accessor.FriendlyByteBufAccessor;
import de.leoxian.moonlightcore.util.ByteBufCodecs;
import de.leoxian.moonlightcore.util.MoonlightRegistries;
import de.leoxian.moonlightcore.util.StreamCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.*;

public record AttachmentChange(AttachmentHolderInfo<?> holderInfo, AttachmentType<?> type, byte[] data) {
    private static final int MAX_PADDING_SIZE_IN_BYTES = AttachmentHolderInfo.MAX_SIZE_IN_BYTES + AttachmentType.MAX_SYNC_ID_SIZE;
    private static final int MAX_DATA_SIZE_IN_BYTES = ByteBufCodecs.SERVERBOUND_MAX_PAYLOAD_SIZE - MAX_PADDING_SIZE_IN_BYTES;

    public static final StreamCodec<ByteBuf, AttachmentChange> STREAM_CODEC = StreamCodec.composite(
            AttachmentHolderInfo.STREAM_CODEC, AttachmentChange::holderInfo,
            AttachmentType.STREAM_CODEC, AttachmentChange::type,
            ByteBufCodecs.BYTE_ARRAY, AttachmentChange::data,
            AttachmentChange::new
    );

    @SuppressWarnings("unchecked")
    public static AttachmentChange create(AttachmentHolderInfo<?> holderInfo, AttachmentType<?> type, Object value) {
        StreamCodec<? super ByteBuf, Object> codec = (StreamCodec<? super ByteBuf, Object>) type.streamCodec();
        Objects.requireNonNull(codec, "Attachment type stream codec cannot be null");

        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        if(value != null){
            byteBuf.writeBoolean(true);
            codec.encode(byteBuf, value);
        } else {
            byteBuf.writeBoolean(false);
        }

        byte[] encoded = byteBuf.array();
        if(encoded.length > MAX_DATA_SIZE_IN_BYTES) {
            throw new IllegalArgumentException("Encoded data for attachment '" + type.id() + "' was too bif (" + encoded.length + " > " + MAX_DATA_SIZE_IN_BYTES + ")");
        }

        return new AttachmentChange(holderInfo, type, encoded);
    }

    public static void partitionAndSendPacket(ServerPlayer player, List<AttachmentChange> changes) {
        changes.sort(Comparator.comparingInt(c -> c.data().length));

        List<AttachmentChange> packetChanges = new ArrayList<>();
        int maxVarIntSize = FriendlyByteBufAccessor.getMAX_VARINT_SIZE();
        int byteSize = maxVarIntSize;

        for(AttachmentChange change : changes) {
            if(!MoonlightRegistries.SYNCED_ATTACHMENT_TYPE.containsKey(change.type().id())) {
                continue;
            }

            int size = MAX_PADDING_SIZE_IN_BYTES + change.data().length;
            if(byteSize + size > MAX_DATA_SIZE_IN_BYTES){
                MoonlightCore.PACKET_DISPATCHER.sendToPlayer(player, new S2CAttachmentSyncPacket(packetChanges));
                packetChanges.clear();
                byteSize = maxVarIntSize;
            }

            packetChanges.add(change);
            byteSize += size;
        }

        if(!packetChanges.isEmpty()) {
            MoonlightCore.PACKET_DISPATCHER.sendToPlayer(player, new S2CAttachmentSyncPacket(packetChanges));
        }
    }

    @SuppressWarnings("unchecked")
    public void applyChanges(Level level) {
        Objects.requireNonNull(this.type.streamCodec(), "Synced attachment type cannot have a null stream codec");
        AttachmentHolder holder = this.holderInfo().getHolder(level);

        if(holder == null) {
            throw new IllegalArgumentException("Received attachment change from unknown holder\n  - Attachment id: '" + this.type().id() + "'\n  - Level: '" + level.dimension() + "'");
        }

        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.copiedBuffer(this.data()));
        Object value = byteBuf.readBoolean() ? this.type().streamCodec().decode(byteBuf) : null;

        holder.setAttachedData((AttachmentType<Object>) this.type(), value);
    }
}
