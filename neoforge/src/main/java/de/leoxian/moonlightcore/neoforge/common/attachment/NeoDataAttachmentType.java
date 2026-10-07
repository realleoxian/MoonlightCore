package de.leoxian.moonlightcore.neoforge.common.attachment;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.common.attachment.DataAttachmentSyncPredicate;
import de.leoxian.moonlightcore.common.attachment.DataAttachmentType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public record NeoDataAttachmentType<T>(Identifier id, Codec<T> persistentCodec, StreamCodec<? super ByteBuf, T> streamCodec, DataAttachmentSyncPredicate syncPredicate, Supplier<T> initializer, boolean copyOnDeath,
									Supplier<AttachmentType<T>> neoAttachment) implements DataAttachmentType<T> {
}
