package de.leoxian.moonlightcore.fabric.common.attachment;

import de.leoxian.moonlightcore.common.attachment.DataAttachmentHolder;
import de.leoxian.moonlightcore.common.attachment.DataAttachmentType;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import org.jspecify.annotations.Nullable;

public record FabricDataAttachmentHolder(AttachmentTarget fabricHolder) implements DataAttachmentHolder {
    @Override
    public @Nullable <T> T get(DataAttachmentType<T> type) {
        return fabricHolder.getAttached(((FabricDataAttachmentType<T>) type).fabricAttachment());
    }

    @Override
    public <T> T getOrCreate(DataAttachmentType<T> type) {
        return fabricHolder.getAttachedOrCreate(((FabricDataAttachmentType<T>) type).fabricAttachment(), type.initializer());
    }

    @Override
    public boolean has(DataAttachmentType<?> type) {
        return fabricHolder.hasAttached(((FabricDataAttachmentType<?>) type).fabricAttachment());
    }

    @Override
    public <T> void set(DataAttachmentType<T> type, T value) {
        fabricHolder.setAttached(((FabricDataAttachmentType<T>) type).fabricAttachment(), value);
    }

    @Override
    public <T> T remove(DataAttachmentType<T> type) {
        return fabricHolder.removeAttached(((FabricDataAttachmentType<T>) type).fabricAttachment());
    }
}
