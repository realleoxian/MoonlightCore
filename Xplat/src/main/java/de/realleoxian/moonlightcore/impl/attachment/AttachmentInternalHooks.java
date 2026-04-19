package de.realleoxian.moonlightcore.impl.attachment;

import de.realleoxian.moonlightcore.api.attachment.AttachmentHolder;
import de.realleoxian.moonlightcore.api.attachment.AttachmentMap;
import de.realleoxian.moonlightcore.api.attachment.AttachmentType;
import de.realleoxian.moonlightcore.api.event.EventPriority;
import de.realleoxian.moonlightcore.api.event.ServerPlayerEvents;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.Internal
public final class AttachmentInternalHooks {

    static {
        ServerPlayerEvents.CLONE.subscribe(EventPriority.HIGHEST, (oldPlayer, newPlayer, wasDeath) -> transfer((AttachmentHolder) oldPlayer, (AttachmentHolder) newPlayer, wasDeath));
    }

    public static void init() {}

    @SuppressWarnings("unchecked")
    private static void transfer(AttachmentHolder from, AttachmentHolder to, boolean wasDeath) {
        Map<AttachmentType<?>, Object> attachedData = from.getAttachmentsMap().getAttachments();
        if(attachedData.isEmpty()) {
            return;
        }

        for(Map.Entry<AttachmentType<?>, Object> attachment : attachedData.entrySet()) {
            AttachmentType<?> type = attachment.getKey();

            if(type.copyOnDeath() || !wasDeath) {
                to.getAttachmentsMap().set((AttachmentType<Object>) type, attachment.getValue());
            }
        }
    }

    static boolean hasPersistentAttachments(AttachmentMap map) {
        var attachments = map.getAttachments();
        if (attachments == null || attachments.isEmpty())
            return false;

        for (var attachment : attachments.entrySet()) {
            if (attachment.getKey().isPersistent()) {
                return true;
            }
        }
        return false;
    }
}
