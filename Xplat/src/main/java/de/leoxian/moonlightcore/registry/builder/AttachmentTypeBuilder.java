package de.leoxian.moonlightcore.registry.builder;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.attachment.AttachmentHolder;
import de.leoxian.moonlightcore.attachment.AttachmentSyncPredicate;
import de.leoxian.moonlightcore.attachment.AttachmentType;
import de.leoxian.moonlightcore.util.MoonlightRegistries;
import de.leoxian.moonlightcore.util.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Supplier;

public class AttachmentTypeBuilder<T> extends AbstractBuilder<AttachmentType<?>, AttachmentType<T>> {

    public static <T> AttachmentTypeBuilder<T> of(ResourceLocation id) {
        return new AttachmentTypeBuilder<>(id);
    }

    private Codec<T> persistentCodec = null;
    private StreamCodec<? super FriendlyByteBuf, T> streamCodec = null;
    private AttachmentSyncPredicate syncPredicate = null;
    private Supplier<T> initialValue = null;
    private boolean copyOnDeath = false;
    private boolean isReadOnly = false;

    protected AttachmentTypeBuilder(ResourceLocation id) {
        super(MoonlightRegistries.Keys.ATTACHMENTS_TYPE, id);
    }

    /**
     * Declares that the attachment should persist between server restarts, using the provided {@link Codec} for de/serialization
     * @param persistentCodec The codec used for de/serialization
     * @return The builder
     */
    public AttachmentTypeBuilder<T> persistentCodec(Codec<T> persistentCodec) {
        this.persistentCodec = Objects.requireNonNull(persistentCodec, "Codec cannot be null");
        return this;
    }

    /**
     * Declares that the attachment can be synced with some clients, as determined by {@code syncPredicate}.
     * @param streamCodec The codec used to de/serialize the attachment data over the network
     * @param syncPredicate An {@link AttachmentSyncPredicate} determining with which clients to synchronize data
     * @return The builder
     */
    public AttachmentTypeBuilder<T> syncWith(StreamCodec<? super FriendlyByteBuf, T> streamCodec, AttachmentSyncPredicate syncPredicate) {
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
    public AttachmentTypeBuilder<T> initialValue(Supplier<T> initialValue) {
        this.initialValue = Objects.requireNonNull(initialValue, "The initial value cannot be null");
        return this;
    }

    /**
     * Declares if the data of the attachment should persist after a player's death
     * @param copyOnDeath Whether the attachment data should copy on death
     * @return The builder
     */
    public AttachmentTypeBuilder<T> copyOnDeath(boolean copyOnDeath) {
        this.copyOnDeath = copyOnDeath;
        return this;
    }

    /**
     * Declares if the attachment is read-only, this means it can only be set or removed but can't change the value of it
     * @param isReadOnly Whether the attachment is read-only
     * @return The builder
     */
    public AttachmentTypeBuilder<T> isReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
        return this;
    }

    @Override
    protected AttachmentType<T> buildEntry() {
        return new AttachmentType<>(this.id(), this.persistentCodec, this.streamCodec, this.syncPredicate, this.initialValue, this.copyOnDeath, this.isReadOnly);
    }
}
