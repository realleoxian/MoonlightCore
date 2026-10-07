package de.leoxian.moonlightcore.common.attachment;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@ApiStatus.NonExtendable
public interface DataAttachmentType<T> {
	static <T> Builder<T> builder(Supplier<T> initializer) {
		return XplatAbstraction.INSTANCE.createAttachmentTypeBuilder(initializer);
	}

	Identifier id();

	@Nullable Codec<T> persistentCodec();

	default boolean isPersistent() {
		return persistentCodec() != null;
	}

	@Nullable DataAttachmentSyncPredicate syncPredicate();

	@Nullable StreamCodec<? super ByteBuf, T> streamCodec();

	default boolean isSynced() {
		return streamCodec() != null && syncPredicate() != null;
	}

	boolean copyOnDeath();

	@Nullable Supplier<T> initializer();

	interface Builder<T> {
		DataAttachmentType.Builder<T> persistent(final Codec<T> codec);

		DataAttachmentType.Builder<T> synced(final StreamCodec<? super ByteBuf, T> streamCodec, final DataAttachmentSyncPredicate syncPredicate);

		DataAttachmentType.Builder<T> copyOnDeath(boolean copyOnDeath);

		DataAttachmentType<T> build(final Identifier id);
	}
}
