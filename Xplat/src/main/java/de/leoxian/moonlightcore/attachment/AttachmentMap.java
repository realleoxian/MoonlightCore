package de.leoxian.moonlightcore.attachment;

import de.leoxian.moonlightcore.util.nullness.Nullable;

/**
 * A convenience wrapper for {@link AttachmentHolder} that provides type-safe access
 * to attached data without requiring explicit casting.
 * <p>
 * This record delegates all attachment operations to the wrapped {@link AttachmentHolder}
 * @param wrapped The wrapped {@link AttachmentHolder} instance that stores the attachments
 */
public record AttachmentMap(AttachmentHolder wrapped) implements AttachmentHolder {

    @Override
    public <T> void syncAttachedData(AttachmentType<T> type) {
        this.wrapped().syncAttachedData(type);
    }

    @Override
    public <T> @Nullable T getAttachedData(AttachmentType<T> type) {
        return this.wrapped().getAttachedData(type);
    }

    @Override
    public <T> @Nullable T setAttachedData(AttachmentType<T> type, @Nullable T value) {
        return this.wrapped().setAttachedData(type, value);
    }

    @Override
    public boolean hasAttacheData(AttachmentType<?> type) {
        return this.wrapped().hasAttacheData(type);
    }

}
