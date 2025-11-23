package de.leoxian.moonlightcore.attachment;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.util.MoonlightRegistries;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.IdentityHashMap;
import java.util.Map;

@ApiStatus.Internal
public class AttachmentInternals {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String NBT_TAG = MoonlightCore.nbt("attachments");

    @SuppressWarnings("unchecked")
    @ApiStatus.Internal
    public static void transfer(AttachmentHolder from, AttachmentHolder to, boolean isDeath) {
        Map<AttachmentType<?>, ?> attachments = ((AttachmentHolderImpl) from).mlcore_getAttachments();

        if(attachments == null) {
            return;
        }

        for(var entry : attachments.entrySet()) {
            AttachmentType<Object> type = (AttachmentType<Object>) entry.getKey();

            if(!isDeath || type.copyOnDeath()) {
                to.setAttachedData(type, entry.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void serializePersistentAttachments(CompoundTag tag, @Nullable IdentityHashMap<AttachmentType<?>, Object> attachments) {
        if(attachments == null || attachments.isEmpty()) {
            return;
        }

        CompoundTag attachmentsTag = new CompoundTag();

        attachments.forEach((type, val) -> {
            if(type.isPersistent()) {
                Codec<Object> codec = (Codec<Object>) type.persistentCodec();

                codec.encodeStart(NbtOps.INSTANCE, val).get()
                        .ifRight(partial -> {
                            LOGGER.error("Unexpected error while serializing attachment '{}'. Error:", type.id());
                            LOGGER.error(partial.message());
                        }).ifLeft(t -> attachmentsTag.put(type.id().toString(), t));
            }
        });

        tag.put(NBT_TAG, attachmentsTag);
    }

    @SuppressWarnings("unchecked")
    public static @Nullable IdentityHashMap<AttachmentType<?>, Object> deserializePersistentAttachments(CompoundTag tag) {
        if(!tag.contains(NBT_TAG, Tag.TAG_COMPOUND)) {
            IdentityHashMap<AttachmentType<?>, Object> attachments = new IdentityHashMap<>();
            CompoundTag attachmentsTag = tag.getCompound(NBT_TAG);

            for(var entryKey : attachmentsTag.getAllKeys()) {
                ResourceLocation attachmentId = new ResourceLocation(entryKey);

                if(!MoonlightRegistries.ATTACHMENT_TYPE.containsKey(attachmentId)) {
                    LOGGER.warn("The attachment '{}' isn't registered, skipping", attachmentId);
                    continue;
                }

                AttachmentType<Object> type = (AttachmentType<Object>) MoonlightRegistries.ATTACHMENT_TYPE.get(attachmentId);

                if(type.isPersistent()) {
                    type.persistentCodec().parse(NbtOps.INSTANCE, attachmentsTag.get(entryKey)).get()
                            .ifRight(partial -> {
                                LOGGER.error("Unexpected error while de-serializing attachment '{}'. Error:", type.id());
                                LOGGER.error(partial.message());
                            }).ifLeft(val -> attachments.put(type, val));
                }
            }

            return attachments;
        }

        return null;
    }

    @ApiStatus.Internal
    public static boolean hasPersistentAttachments(AttachmentHolderImpl holder) {
        Map<AttachmentType<?>, ?> attachments = holder.mlcore_getAttachments();
        if(attachments == null || attachments.isEmpty()) {
            return false;
        }

        for(var attachment : attachments.keySet()) {
            if(attachment.isPersistent()) {
                return true;
            }
        }

        return false;
    }

    private AttachmentInternals() {}
}
