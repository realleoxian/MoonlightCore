package de.leoxian.moonlightcore.attachment;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.util.ByteBufCodecs;
import de.leoxian.moonlightcore.util.MoonlightRegistries;
import de.leoxian.moonlightcore.util.StreamCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents a data attachment type: some data that can be added to any object implementing {@link AttachmentHolder} (and {@link AttachmentHolderImpl} (INTERNAL USE)).
 * The data attachment types must be registered on {@link MoonlightRegistries#ATTACHMENT_TYPE the registry}, and, in case of synced,
 * {@link MoonlightRegistries#SYNCED_ATTACHMENT_TYPE the synced registry too}
 * @param <T> The type of data
 */
public class AttachmentType<T> {
    public static final int MAX_SYNC_ID_SIZE = 256;
    public static final StreamCodec<ByteBuf, AttachmentType<?>> STREAM_CODEC = ByteBufCodecs.RESOURCE_LOCATION.xmap(id -> Objects.requireNonNull(MoonlightRegistries.ATTACHMENT_TYPE.get(id)), AttachmentType::id);

    /**
     * Creates a new {@link AttachmentType} with the given id and using a {@linkplain Builder}
     * @param id THe id of the attachment
     * @param builderCallback A lambda that configures a {@link Builder} for this attachment type
     * @param <T> THe type of attached data
     * @return A new {@link AttachmentType} instance
     */
    public static <T> AttachmentType<T> create(ResourceLocation id, Consumer<AttachmentType.Builder<T>> builderCallback) {
        Builder<T> builder = new Builder<>();
        builderCallback.accept(builder);

        return new AttachmentType<>(id, builder);
    }

    private final ResourceLocation id;
    private final Codec<T> persistentCodec;
    private final StreamCodec<? super FriendlyByteBuf, T> streamCodec;
    private final AttachmentSyncPredicate syncPredicate;
    private final Supplier<T> initialValue;
    private final boolean copyOnDeath;
    private final boolean isReadOnly;

    private AttachmentType(ResourceLocation id, Builder<T> builder) {
        this.id = Objects.requireNonNull(id, "Attachment type id can't be null");

        this.persistentCodec = builder.persistentCodec;
        this.streamCodec = builder.streamCodec;
        this.syncPredicate = builder.syncPredicate;
        this.initialValue = builder.initialValue;
        this.copyOnDeath = builder.copyOnDeath;
        this.isReadOnly = builder.isReadOnly;

        if(this.syncPredicate != null && id.toString().length() > MAX_SYNC_ID_SIZE) {
            throw new IllegalArgumentException("Identifier length is too long for a synced attachment type (" + id().toString().length() + " > " + MAX_SYNC_ID_SIZE + ")");
        }
    }

    /**
     * @return The identifier that uniquely identifies this attachment
     */
    public ResourceLocation id() {
        return this.id;
    }

    /**
     * @return An optional {@link Codec} used for de/serializing this attachment into NBT for persistence, may be {@code null}
     */
    public Codec<T> persistentCodec() {
        return this.persistentCodec;
    }

    /**
     * @return An optional {@link StreamCodec} used for de/serializing this attachment into a {@link FriendlyByteBuf} for synchronization, may be {@code null}
     */
    public StreamCodec<? super FriendlyByteBuf, T> streamCodec() {
        return this.streamCodec;
    }

    /**
     * @return An optional {@link AttachmentSyncPredicate} used to check if a {@link AttachmentHolder}
     * and a {@link net.minecraft.server.level.ServerPlayer} can be synced, may be {@code null}
     */
    public AttachmentSyncPredicate syncPredicate() {
        return this.syncPredicate;
    }

    /**
     * @return The initializer for this attachment, may be {@code null}
     */
    public Supplier<T> initialValue(){
        return this.initialValue;
    }

    /**
     * @return Whether this attachment should persist after an entity dies
     */
    public boolean copyOnDeath() {
        return this.copyOnDeath;
    }

    /**
     * @return Whether this attachment is read-only, this means you can remove or set it but you can't change the value of it
     */
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
     * @return Whether this attachment can be synced with clients
     */
    public boolean isSynced() {
        return this.syncPredicate != null;
    }

    public static class Builder<T> {
        private Codec<T> persistentCodec = null;
        private StreamCodec<? super FriendlyByteBuf, T> streamCodec = null;
        private AttachmentSyncPredicate syncPredicate = null;
        private Supplier<T> initialValue = null;
        private boolean copyOnDeath = false;
        private boolean isReadOnly = false;

        private Builder() {}

        /**
         * Declares that the attachment should persist between server restarts, using the provided {@link Codec} for de/serialization
         * @param persistentCodec The codec used for de/serialization
         * @return The builder
         */
        public Builder<T> persistentCodec(Codec<T> persistentCodec) {
            this.persistentCodec = Objects.requireNonNull(persistentCodec, "Codec cannot be null");
            return this;
        }

        /**
         * Declares that the attachment can be synced with some clients, as determined by {@code syncPredicate}.
         * @param streamCodec The codec used to de/serialize the attachment data over the network
         * @param syncPredicate An {@link AttachmentSyncPredicate} determining with which clients to synchronize data
         * @return The builder
         */
        public Builder<T> syncWith(StreamCodec<? super FriendlyByteBuf, T> streamCodec, AttachmentSyncPredicate syncPredicate) {
            this.streamCodec = Objects.requireNonNull(streamCodec, "Stream codec cannot be null");
            this.syncPredicate = Objects.requireNonNull(syncPredicate, "Attachment sync predicate cannot be null");

            return this;
        }

        /**
         * Declares an initial/default value for the attachment type. The initializer will be called by {@link AttachmentHolder#getAttachedDataOrSet(AttachmentType)}
         * to automatically initialize the attachments that don't yet exist. It must not return {@code null}
         * @param initialValue The initial value supplier
         * @return The builder
         */
        public Builder<T> initialValue(Supplier<T> initialValue) {
            this.initialValue = Objects.requireNonNull(initialValue, "The initial value cannot be null");
            return this;
        }

        /**
         * Declares if the data of the attachment should persist after a player's death
         * @param copyOnDeath Whether the attachment data should copy on death
         * @return The builder
         */
        public Builder<T> copyOnDeath(boolean copyOnDeath) {
            this.copyOnDeath = copyOnDeath;
            return this;
        }

        /**
         * Declares if the attachment is read-only, this means it can only be set or removed but can't change the value of it
         * @param isReadOnly Whether the attachment is read-only
         * @return The builder
         */
        public Builder<T> isReadOnly(boolean isReadOnly) {
            this.isReadOnly = isReadOnly;
            return this;
        }
    }
}
