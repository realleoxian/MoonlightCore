package de.leoxian.moonlightcore.common.attachment;

import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public interface DataAttachmentSyncPredicate {
    static DataAttachmentSyncPredicate all() {
        return (_, _) -> true;
    }

    static DataAttachmentSyncPredicate allButHolder() {
        return (holder, player) -> !Objects.equals(holder, player);
    }

    static DataAttachmentSyncPredicate targetOnly() {
        return (holder, player) -> Objects.equals(holder, player);
    }

    boolean test(DataAttachmentHolder holder, ServerPlayer player);
}
