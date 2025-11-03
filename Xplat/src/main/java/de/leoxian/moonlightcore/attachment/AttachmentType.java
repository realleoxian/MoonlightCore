package de.leoxian.moonlightcore.attachment;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.util.ByteBufCodecs;
import de.leoxian.moonlightcore.util.MoonlightRegistries;
import de.leoxian.moonlightcore.util.StreamCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Represents a data attachment type: some data that can be added to any object implementing {@link AttachmentHolder} (and {@link AttachmentHolderImpl} (INTERNAL USE)).
 * The data attachment types must be registered on {@link MoonlightRegistries#ATTACHMENT_TYPE the registry}, and, in case of synced,
 * {@link MoonlightRegistries#SYNCED_ATTACHMENT_TYPE the synced registry too}
 *
 * @param <T> The type of data
 */
public record AttachmentType<T>(ResourceLocation id, Codec<T> persistentCodec, StreamCodec<? super FriendlyByteBuf, T> streamCodec, AttachmentSyncPredicate syncPredicate, Supplier<T> initialValue, boolean copyOnDeath, boolean isReadOnly) {
    public static final int MAX_SYNC_ID_SIZE = 256;
    public static final StreamCodec<ByteBuf, AttachmentType<?>> STREAM_CODEC = ByteBufCodecs.RESOURCE_LOCATION.xmap(id -> Objects.requireNonNull(MoonlightRegistries.ATTACHMENT_TYPE.get(id)), AttachmentType::id);

    public AttachmentType(ResourceLocation id, Codec<T> persistentCodec, StreamCodec<? super FriendlyByteBuf, T> streamCodec, AttachmentSyncPredicate syncPredicate, Supplier<T> initialValue, boolean copyOnDeath, boolean isReadOnly) {
        this.id = Objects.requireNonNull(id, "Attachment type id can't be null");

        this.persistentCodec = persistentCodec;
        this.streamCodec = streamCodec;
        this.syncPredicate = syncPredicate;
        this.initialValue = initialValue;
        this.copyOnDeath = copyOnDeath;
        this.isReadOnly = isReadOnly;

        if (this.syncPredicate != null && id.toString().length() > MAX_SYNC_ID_SIZE) {
            throw new IllegalArgumentException("Id length is too long for a synced attachment type (" + id().toString().length() + " > " + MAX_SYNC_ID_SIZE + ")");
        }
    }

    /**
     * @return The identifier that uniquely identifies this attachment
     */
    @Override
    public ResourceLocation id() {
        return this.id;
    }

    /**
     * @return An optional {@link Codec} used for de/serializing this attachment into NBT for persistence, may be {@code null}
     */
    @Override
    public Codec<T> persistentCodec() {
        return this.persistentCodec;
    }

    /**
     * @return An optional {@link StreamCodec} used for de/serializing this attachment into a {@link FriendlyByteBuf} for synchronization, may be {@code null}
     */
    @Override
    public StreamCodec<? super FriendlyByteBuf, T> streamCodec() {
        return this.streamCodec;
    }

    /**
     * @return An optional {@link AttachmentSyncPredicate} used to check if a {@link AttachmentHolder}
     * and a {@link ServerPlayer} can be synced, may be {@code null}
     */
    @Override
    public AttachmentSyncPredicate syncPredicate() {
        return this.syncPredicate;
    }

    /**
     * @return The initializer for this attachment, may be {@code null}
     */
    @Override
    public Supplier<T> initialValue() {
        return this.initialValue;
    }

    /**
     * @return Whether this attachment should persist after an entity dies
     */
    @Override
    public boolean copyOnDeath() {
        return this.copyOnDeath;
    }

    /**
     * @return Whether this attachment is read-only, this means you can remove or set it but you can't change the value of it
     */
    @Override
    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    /**
     * @return Whether this attachment will persist across server restarts
     */
    public boolean isPersistent() {
        return this.persistentCodec != null;
    }

    /**
     * Whether the attachment can be synced with clients. This DOESN'T mean that it will sync automatically,
     * if you want to sync the associated data with the attachment you should call {@link AttachmentHolder#syncAttachedData(AttachmentType)}
     *
     * @return Whether this attachment can be synced with clients
     */
    public boolean isSynced() {
        return this.syncPredicate != null;
    }
}
