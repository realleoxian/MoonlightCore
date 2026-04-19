package de.realleoxian.moonlightcore.impl.attachment;

import de.realleoxian.moonlightcore.api.attachment.AttachmentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public final class AttachmentPersistentData extends SavedData {
    public static final String ID = "moonlightcore_attachments";

    public static AttachmentPersistentData read(ServerLevel level, CompoundTag tag) {
        AttachmentMap.get(level).readFromNBT(tag);
        return new AttachmentPersistentData(level);
    }

    private final AttachmentMap attachmentMap;
    private final boolean wasSerialized;

    public AttachmentPersistentData(ServerLevel level) {
        this.attachmentMap = AttachmentMap.get(level);
        this.wasSerialized = AttachmentInternalHooks.hasPersistentAttachments(this.attachmentMap);
     }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        this.attachmentMap.writeToNBT(compoundTag);
        return compoundTag;
    }

    @Override
    public boolean isDirty() {
        return this.wasSerialized || AttachmentInternalHooks.hasPersistentAttachments(this.attachmentMap);
    }
}
