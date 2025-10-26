package de.leoxian.moonlightcore.attachment;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class AttachmentSavedState extends SavedData {
    public static final String ID = "moonlightcore_attachments";

    private final AttachmentHolderImpl holder;
    private final boolean wasSerialized;

    public static AttachmentSavedState read(ServerLevel level, CompoundTag nbt) {
        ((AttachmentHolderImpl) level).mlcore_readPersistentAttachments(nbt);
        return new AttachmentSavedState(level);
    }

    public AttachmentSavedState(ServerLevel level) {
        this.holder = (AttachmentHolderImpl) level;
        this.wasSerialized = AttachmentInternals.hasPersistentAttachments((AttachmentHolderImpl) level);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        this.holder.mlcore_writePersistentAttachments(compoundTag);
        return compoundTag;
    }

    @Override
    public boolean isDirty() {
        return this.wasSerialized || AttachmentInternals.hasPersistentAttachments(this.holder);
    }
}
