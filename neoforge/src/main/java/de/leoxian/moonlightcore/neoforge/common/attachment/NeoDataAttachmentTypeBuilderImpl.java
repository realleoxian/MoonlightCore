package de.leoxian.moonlightcore.neoforge.common.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.leoxian.moonlightcore.common.attachment.DataAttachmentSyncPredicate;
import de.leoxian.moonlightcore.common.attachment.DataAttachmentType;
import de.leoxian.moonlightcore.neoforge.common.ModDeferredRegisters;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentCopyHandler;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class NeoDataAttachmentTypeBuilderImpl<T> implements DataAttachmentType.Builder<T> {
    private @Nullable Codec<T> persistentCodec = null;
    private @Nullable StreamCodec<? super ByteBuf, T> streamCodec = null;
    private @Nullable DataAttachmentSyncPredicate syncPredicate = null;
    private boolean copyOnDeath = false;
    private final Supplier<T> initializer;

    public NeoDataAttachmentTypeBuilderImpl(Supplier<T> initializer) {
        this.initializer = initializer;
    }

    @Override
    public DataAttachmentType.Builder<T> persistent(Codec<T> codec) {
        this.persistentCodec = Objects.requireNonNull(codec, "May not set a 'null' persistent codec");
        return this;
    }

    @Override
    public DataAttachmentType.Builder<T> synced(StreamCodec<? super ByteBuf, T> streamCodec, DataAttachmentSyncPredicate syncPredicate) {
        if (streamCodec != null && syncPredicate == null) {
            throw new IllegalStateException("May not set a stream codec for sync but not a sync predicate");
        } else if (streamCodec == null && syncPredicate != null) {
            throw new IllegalStateException("May not set a sync predicate but not a stream codec");
        }

        this.streamCodec = streamCodec;
        this.syncPredicate = syncPredicate;
        return this;
    }

    @Override
    public DataAttachmentType.Builder<T> copyOnDeath(boolean copyOnDeath) {
        this.copyOnDeath = copyOnDeath;
        return this;
    }

    @Override
    public DataAttachmentType<T> build(Identifier id) {
        Supplier<AttachmentType<T>> neoAttachment = ModDeferredRegisters.get(NeoForgeRegistries.ATTACHMENT_TYPES, id.getNamespace())
                .register(id.getPath(), () -> {
                    AttachmentType.Builder<T> builder = AttachmentType.builder(this.initializer);
                    if (this.persistentCodec != null) {
                        builder = builder.serialize(MapCodec.assumeMapUnsafe(this.persistentCodec));
                    }

                    if (this.streamCodec != null && this.syncPredicate != null) {
                        builder = builder.sync(new NeoDataAttachmentSyncHandler<>(this.streamCodec, this.syncPredicate));
                    }

                    if (copyOnDeath) {
                        builder = builder.copyOnDeath();
                    }

                    return builder.build();
                });

        return new NeoDataAttachmentType<>(id, this.persistentCodec, this.streamCodec, this.syncPredicate, this.initializer, this.copyOnDeath, neoAttachment);
    }
}
