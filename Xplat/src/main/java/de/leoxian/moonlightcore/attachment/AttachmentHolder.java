package de.leoxian.moonlightcore.attachment;

import de.leoxian.moonlightcore.util.nullness.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public interface AttachmentHolder {
    /**
     * Syncs the data of the given {@link AttachmentType} with the clients that may sync ({@link AttachmentSyncPredicate})
     * @param type The {@link AttachmentType}
     * @param <T> The type of attachment
     */
    default <T> void syncAttachedData(AttachmentType<T> type) {
        throw new AssertionError();
    }

    /**
     * Get the current associated data of the given {@link AttachmentType}, may be {@code null}
     * @param type The {@link AttachmentType} that may have associated data
     * @param <T> The type of data
     */
    @Nullable
    default <T> T getAttachedData(AttachmentType<T> type) {
        throw new AssertionError();
    }

    /**
     * Sets the data associated with the given {@link AttachmentType}. Passing {@code null} will remove any associated data with the {@link AttachmentType}.
     * <p>
     * This method also returns the previous value of the {@link AttachmentType}, may be {@code null}
     * @param type The {@link AttachmentType} that will have the associated data
     * @param value The new associated data
     * @throws IllegalArgumentException If trying to change the value of a read-only {@link AttachmentType}
     * @param <T> The type of data
     */
    @Nullable
    default <T> T setAttachedData(AttachmentType<T> type, @Nullable T value) {
        throw new AssertionError();
    }

    /**
     * Whether the given {@link AttachmentType} has any associated data to it
     * @param type The {@link AttachmentType}
     */
    default boolean hasAttacheData(AttachmentType<?> type) {
        throw new AssertionError();
    }

    /**
     * Removes any associated data to the given {@link AttachmentType}. Equivalent to call {@link #setAttachedData(AttachmentType, Object)} with {@code null}
     * @param type The {@link AttachmentType}
     * @return The previous associated data with the {@link AttachmentType}
     * @param <T> The type of data
     */
    default <T> T removeAttachedData(AttachmentType<T> type) {
        return setAttachedData(type, null);
    }

    /**
     * Get the current associated data of the given {@link AttachmentType}, if no associated data present will throw {@link IllegalStateException}
     * @param type The {@link AttachmentType}
     * @param <T> The type of data
     * @throws IllegalStateException If the given {@link AttachmentType} doesn't have any associated data to it
     * @return The current associated data with the attachment
     */
    default <T> T getAttachedDataOrThrow(AttachmentType<T> type) {
        if(!this.hasAttacheData(type)) {
            throw new IllegalStateException("The attachment '" + type.id() + "' doesn't have any attached data");
        }

        return getAttachedData(type);
    }

    /**
     * Get the current associated data of the given {@link AttachmentType} if there is any associated to it, if not,
     * the attachment will be initialized with the given initial value
     * @param type The {@link AttachmentType}
     * @param initialValue The value that may be associated to the attachment if there isn't any data associated
     * @param <T> The type of data
     * @return The current associated data to the attachment
     */
    default <T> T getAttachedDataOrSet(AttachmentType<T> type, Supplier<T> initialValue) {
        T existing = getAttachedData(type);

        if(existing == null) {
            T initial = Objects.requireNonNull(initialValue.get(), "THe initial data of a attachment cannot be null");
            setAttachedData(type, initial);

            return initial;
        }

        return existing;
    }

    /**
     * Get the current associated data of the given {@link AttachmentType} if there is any associated to it, if not,
     * the attachment will be initialized with its initial value
     * @param type The {@link AttachmentType}
     * @param <T> The type of data
     * @throws NullPointerException If the given {@link AttachmentType} doesn't have a initial value
     * @return The current associated data with the attachment
     */
    default <T> T getAttachedDataOrSet(AttachmentType<T> type) {
        Objects.requireNonNull(type, "getAttachedDataOrSet of one parameter only works with attachment types that have initial value suppliers");
        return getAttachedDataOrSet(type, type.initialValue());
    }
}
