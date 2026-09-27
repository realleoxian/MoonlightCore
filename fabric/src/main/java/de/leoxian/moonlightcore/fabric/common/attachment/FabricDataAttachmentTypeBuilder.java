package de.leoxian.moonlightcore.fabric.common.attachment;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.common.attachment.DataAttachmentSyncPredicate;
import de.leoxian.moonlightcore.common.attachment.DataAttachmentType;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class FabricDataAttachmentTypeBuilder<T> implements DataAttachmentType.Builder<T> {
    private @Nullable Codec<T> persistentCodec = null;
    private @Nullable StreamCodec<? super ByteBuf, T> streamCodec = null;
    private @Nullable DataAttachmentSyncPredicate syncPredicate = null;
    private boolean copyOnDeath = false;
    private final Supplier<T> initializer;

    public FabricDataAttachmentTypeBuilder(Supplier<T> initializer) {
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
        AttachmentType<T> fabricAttachment = AttachmentRegistry.create(id, builder -> {
            builder.initializer(this.initializer);

            if (this.persistentCodec != null)
                builder.persistent(this.persistentCodec);
            if (this.streamCodec != null && this.syncPredicate != null)
                builder.syncWith(streamCodec.cast(), (h, p) -> this.syncPredicate.test(new FabricDataAttachmentHolder(h), p));
            if (this.copyOnDeath)
                builder.copyOnDeath();
        });

        return new FabricDataAttachmentType<>(id, this.persistentCodec, this.streamCodec, this.syncPredicate, this.initializer, this.copyOnDeath, fabricAttachment);
    }
}
