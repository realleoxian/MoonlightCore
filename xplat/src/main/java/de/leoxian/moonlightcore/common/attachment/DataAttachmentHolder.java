package de.leoxian.moonlightcore.common.attachment;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public interface DataAttachmentHolder {
	static DataAttachmentHolder of(final Level level) {
		return XplatAbstraction.INSTANCE.getDataAttachmentsFromLevel(level);
	}

	static DataAttachmentHolder of(final ChunkAccess chunkAccess) {
		return XplatAbstraction.INSTANCE.getDataAttachmentsFromChunk(chunkAccess);
	}

	static DataAttachmentHolder of(final BlockEntity blockEntity) {
		return XplatAbstraction.INSTANCE.getDataAttachmentsFromBlockEntity(blockEntity);
	}

	static DataAttachmentHolder of(final Entity entity) {
		return XplatAbstraction.INSTANCE.getDataAttachmentsFromEntity(entity);
	}

	<T> @Nullable T get(final DataAttachmentType<T> type);

	<T> T getOrCreate(final DataAttachmentType<T> type);

	boolean has(final DataAttachmentType<?> type);

	<T> void set(final DataAttachmentType<T> type, final T value);

	<T> T remove(final DataAttachmentType<T> type);

	default <T> T getOrElse(final DataAttachmentType<T> type, Supplier<T> defValue) {
		T ret = get(type);
		return ret != null ? ret : defValue.get();
	}

	default <T> T getOrThrow(final DataAttachmentType<T> type) {
		return Objects.requireNonNull(get(type), "DataAttachmentType not present: " + type.id());
	}
}
