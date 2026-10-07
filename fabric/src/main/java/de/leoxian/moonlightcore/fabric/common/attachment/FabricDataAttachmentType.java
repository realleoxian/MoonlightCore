package de.leoxian.moonlightcore.fabric.common.attachment;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.common.attachment.DataAttachmentSyncPredicate;
import de.leoxian.moonlightcore.common.attachment.DataAttachmentType;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public record FabricDataAttachmentType<T>(Identifier id, Codec<T> persistentCodec, StreamCodec<? super ByteBuf, T> streamCodec, DataAttachmentSyncPredicate syncPredicate, Supplier<T> initializer, boolean copyOnDeath,
										AttachmentType<T> fabricAttachment) implements DataAttachmentType<T> {
}
