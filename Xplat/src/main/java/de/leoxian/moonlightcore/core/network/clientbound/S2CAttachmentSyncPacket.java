package de.leoxian.moonlightcore.core.network.clientbound;

import de.leoxian.moonlightcore.attachment.sync.AttachmentChange;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.network.CustomPacket;
import de.leoxian.moonlightcore.util.ByteBufCodecs;
import de.leoxian.moonlightcore.util.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record S2CAttachmentSyncPacket(List<AttachmentChange> changes) implements CustomPacket<S2CAttachmentSyncPacket> {
    public static final ResourceLocation ID = MoonlightCore.location("attachment_sync");
    public static final StreamCodec<FriendlyByteBuf, S2CAttachmentSyncPacket> CODEC = StreamCodec.composite(
            AttachmentChange.STREAM_CODEC.map(ByteBufCodecs.toList()), S2CAttachmentSyncPacket::changes,
            S2CAttachmentSyncPacket::new
    );

    @Override
    public StreamCodec<FriendlyByteBuf, S2CAttachmentSyncPacket> codec() {
        return CODEC;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
