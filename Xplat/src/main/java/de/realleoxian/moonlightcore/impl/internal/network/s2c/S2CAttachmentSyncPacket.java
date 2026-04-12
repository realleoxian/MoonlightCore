package de.realleoxian.moonlightcore.impl.internal.network.s2c;

import de.realleoxian.moonlightcore.api.MoonlightCore;
import de.realleoxian.moonlightcore.api.attachment.AttachmentType;
import de.realleoxian.moonlightcore.api.attachment.AttachmentsHolderInfo;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.network.PacketType;
import de.realleoxian.moonlightcore.impl.attachment.sync.AttachmentSyncChange;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record S2CAttachmentSyncPacket(AttachmentSyncChange change) {
    public static final PacketType<S2CAttachmentSyncPacket> TYPE = new PacketType<>(new ResourceLocation("moonlightcore", "attachment_sync"), S2CAttachmentSyncPacket.class, S2CAttachmentSyncPacket::writeToBuffer, S2CAttachmentSyncPacket::readFromBuffer);

    public static void handle(NetworkHelper.PacketContext context, S2CAttachmentSyncPacket packet) {
        context.queueWork(() -> {
            Player player = context.player();
            packet.change().applyChange(player.level());
        });
    }

    public static void writeToBuffer(FriendlyByteBuf byteBuf, S2CAttachmentSyncPacket packet) {
        AttachmentSyncChange change = packet.change();

        AttachmentsHolderInfo<?, ?> holderInfo = packet.change().holderInfo();
        byteBuf.writeByte(holderInfo.typeId());
        holderInfo.writeToBuffer(byteBuf);

        byteBuf.writeResourceLocation(change.type().name());
        byteBuf.writeByteArray(change.data());
    }

    public static S2CAttachmentSyncPacket readFromBuffer(FriendlyByteBuf byteBuf) {
        byte holderType = byteBuf.readByte();
        AttachmentsHolderInfo<?, ?> holderInfo = AttachmentsHolderInfo.Type.getDecoder(holderType).read(byteBuf);
        AttachmentType<?> attachmentType = MoonlightCore.ATTACHMENT_TYPE_REGISTRY.get().get(byteBuf.readResourceLocation());
        byte[] data = byteBuf.readByteArray();

        return new S2CAttachmentSyncPacket(new AttachmentSyncChange(holderInfo, attachmentType, data));
    }
}
