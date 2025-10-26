package de.leoxian.moonlightcore.attachment;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiPredicate;

@ApiStatus.NonExtendable
public interface AttachmentSyncPredicate extends BiPredicate<AttachmentHolder, ServerPlayer> {
    /**
     * A predicate that syncs an attachment with all clients
     */
    AttachmentSyncPredicate ALL = (h, p) -> true;

    /**
     * A predicate that syncs an attachment only with the target it is attached to, when that is a player.
     * If the target isn't a player, the attachment will be synced with no clients
     */
    AttachmentSyncPredicate TARGET_ONLY = (h, p) -> h == p;

    /**
     * A predicate that syncs an attachment with every client except the target it is attached to, when that is a player.
     * When the target isn't a player, the attachment will be synced with all clients
     */
    AttachmentSyncPredicate ALL_BUT_TARGET = (h, p) -> h != p;
}
