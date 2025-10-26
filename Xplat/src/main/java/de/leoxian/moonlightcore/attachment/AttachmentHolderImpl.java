package de.leoxian.moonlightcore.attachment;

import de.leoxian.moonlightcore.attachment.sync.AttachmentChange;
import de.leoxian.moonlightcore.attachment.sync.AttachmentHolderInfo;
import de.leoxian.moonlightcore.core.network.clientbound.S2CAttachmentSyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.function.Consumer;

public interface AttachmentHolderImpl extends AttachmentHolder {

    default void mlcore_writePersistentAttachments(CompoundTag nbt) {}

    default void mlcore_readPersistentAttachments(CompoundTag nbt) {}

    default void mlcore_markDirty() {}

    default void mlcore_sendChangePacket(AttachmentType<?> type, S2CAttachmentSyncPacket packet) {}

    default void mlcore_computeInitialAttachmentChanges(ServerPlayer player, Consumer<AttachmentChange> changeOutput) {}

    default Map<AttachmentType<?>, Object> mlcore_getAttachments() {
        return null;
    }

    default AttachmentHolderInfo<?> mlcore_getHolderInfo() {
        return null;
    }

    default boolean mlcore_shouldSync() {
        return false;
    }

}
