package de.leoxian.moonlightcore.neoforge.common.attachment;

import de.leoxian.moonlightcore.common.attachment.DataAttachmentSyncPredicate;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jspecify.annotations.Nullable;

public record NeoDataAttachmentSyncHandler<T>(StreamCodec<? super ByteBuf, T> streamCodec, DataAttachmentSyncPredicate syncPredicate) implements AttachmentSyncHandler<T> {
	@Override
	public boolean sendToPlayer(IAttachmentHolder holder, ServerPlayer to) {
		return syncPredicate.test(new NeoAttachmentHolderWrapper(holder), to);
	}

	@Override
	public void write(RegistryFriendlyByteBuf buf, T attachment, boolean initialSync) {
		streamCodec.encode(buf, attachment);
	}

	@Override
	public @Nullable T read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable T previousValue) {
		return streamCodec.decode(buf);
	}
}
