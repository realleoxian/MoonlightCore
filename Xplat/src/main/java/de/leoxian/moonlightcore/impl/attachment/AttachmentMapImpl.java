package de.leoxian.moonlightcore.impl.attachment;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.api.MoonlightCore;
import de.leoxian.moonlightcore.api.attachment.AttachmentsHolderInfo;
import de.leoxian.moonlightcore.api.attachment.AttachmentMap;
import de.leoxian.moonlightcore.api.attachment.AttachmentType;
import de.leoxian.moonlightcore.impl.attachment.sync.AttachmentSyncChange;
import de.leoxian.moonlightcore.impl.internal.ModInternal;
import de.leoxian.moonlightcore.impl.internal.ModLoggingMarkers;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.Supplier;

import static de.leoxian.moonlightcore.impl.internal.ModInternal.nbtPrefix;

public final class AttachmentMapImpl implements AttachmentMap {
    public static final String NBT_TAG = nbtPrefix("attachments");
    private static final String NBT_NAME_TAG = "name";
    private static final String NBT_VALUE_TAG = "value";

    public static AttachmentMap create(AttachmentsHolderInfo<?, ?> holderInfo) {
        return new AttachmentMapImpl(holderInfo);
    }

    private final LinkedHashMap<AttachmentType<?>, Object> attachedData = new LinkedHashMap<>();
    private final Map<AttachmentType<?>, Object> attachedDataView = Collections.unmodifiableMap(attachedData);

    private final LinkedHashMap<AttachmentType<?>, AttachmentSyncChange> awaitingSyncChanges = new LinkedHashMap<>();

    private final AttachmentsHolderInfo<?, ?> holderInfo;

    private AttachmentMapImpl(AttachmentsHolderInfo<?, ?> holderInfo) {
        Objects.requireNonNull(holderInfo, "AttachmentsHolderInfo may not be 'null'");
        this.holderInfo = holderInfo;
    }

    @Override
    public <A> void syncAttachment(Level level, AttachmentType<A> type) {
        if(level.isClientSide) {
            return;
        }

        AttachmentSyncChange change = awaitingSyncChanges.remove(type);
        if(change == null) {
            throw new IllegalArgumentException("There is no data awaiting to be synced for attachment '" + type.name() + "'");
        }

        holderInfo.syncAttachment((ServerLevel) level, change);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeToNBT(CompoundTag tag) {
        if(attachedData.isEmpty()) {
            return;
        }

        ListTag attachmentsTag = new ListTag();
        for(Map.Entry<AttachmentType<?>, Object> entry : attachedData.entrySet()) {
            AttachmentType<Object> type = (AttachmentType<Object>) entry.getKey();
            Object value = entry.getValue();

            Codec<Object> codec;
            if((codec = type.codec()) == null) {
                continue;
            }

            codec.encodeStart(NbtOps.INSTANCE, value).get()
                    .ifRight(partial -> {
                        ModInternal.LOGGER.warn(ModLoggingMarkers.ATTACHMENT, "Failed to encode attachment type '{}'. Error: ", type.name());
                        ModInternal.LOGGER.warn(ModLoggingMarkers.ATTACHMENT, partial.message());
                    }).ifLeft(t -> {
                        CompoundTag attachment = new CompoundTag();
                        attachment.putString(NBT_NAME_TAG, type.name().toString());
                        attachment.put(NBT_VALUE_TAG, t);

                        attachmentsTag.add(attachment);
                    });
        }
        tag.put(NBT_TAG, attachmentsTag);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readFromNBT(CompoundTag tag) {
        if(!tag.contains(NBT_TAG, Tag.TAG_LIST)) {
            return;
        }

        IdentityHashMap<AttachmentType<?>, Object> attachedData = new IdentityHashMap<>();
        ListTag attachmentsTag = tag.getList(NBT_TAG, Tag.TAG_COMPOUND);
        for(int i = 0; i < attachmentsTag.size(); i++) {
            CompoundTag attachmentTag = attachmentsTag.getCompound(i);

            ResourceLocation name = new ResourceLocation(attachmentTag.getString(NBT_NAME_TAG));
            if(!MoonlightCore.ATTACHMENT_TYPES.get().containsKey(name)) {
                ModInternal.LOGGER.warn(ModLoggingMarkers.ATTACHMENT, "Attachment type '{}' isn't registered, cannot be read", name);
                continue;
            }

            AttachmentType<Object> attachmentType = (AttachmentType<Object>) MoonlightCore.ATTACHMENT_TYPES.get().get(name);
            Codec<Object> codec;
            if((codec = attachmentType.codec()) == null) {
                continue;
            }

            Tag valueTag = attachmentTag.get(NBT_VALUE_TAG);
            codec.parse(NbtOps.INSTANCE, valueTag).get()
                    .ifRight(partial -> {
                        ModInternal.LOGGER.warn(ModLoggingMarkers.ATTACHMENT, "Failed to decode attachment type '{}'. Error: ", name);
                        ModInternal.LOGGER.warn(ModLoggingMarkers.ATTACHMENT, partial.message());
                    }).ifLeft(val -> attachedData.put(attachmentType, val));
        }

        this.attachedData.clear();
        this.attachedData.putAll(attachedData);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> @Nullable A set(AttachmentType<A> type, @Nullable A newValue) {
        Objects.requireNonNull(type, "Attachment type cannot be 'null'");

        if(!MoonlightCore.ATTACHMENT_TYPES.get().containsKey(type.name())) {
            throw new IllegalArgumentException("Unregistered attachment type: " + type.name());
        }

        A oldData;
        if(newValue == null) {
            if(attachedData.isEmpty() || !attachedData.containsKey(type)) {
                return null;
            }

            oldData = (A) attachedData.remove(type);
        } else {
            oldData = (A) attachedData.put(type, newValue);
        }

        if(type.isSync()) {
            awaitingSyncChanges.put(type, AttachmentSyncChange.create(holderInfo, type, newValue));
        }

        return oldData;
    }

    @Override
    public <A> @Nullable A remove(AttachmentType<A> type) {
        return set(type, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> @Nullable A get(AttachmentType<A> type) {
        return (A) attachedData.get(type);
    }

    @Override
    public <A> A getOrSet(AttachmentType<A> type, Supplier<A> initializer) {
        Objects.requireNonNull(type, "AttachmentType cannot be 'null'");
        Objects.requireNonNull(initializer, "Attachmet data initializer cannot be 'null'");

        A ret = get(type);
        if(ret == null) {
            set(type, initializer.get());
            ret = get(type);

            if(ret == null) {
                throw new IllegalStateException("Couldn't get any data even after initializing it");
            }
        }

        return ret;
    }

    @Override
    public <A> A getOrSet(AttachmentType<A> type) {
        return getOrSet(type, type.initializer());
    }

    @Override
    public <A> A getOrThrow(AttachmentType<A> type) {
        return Objects.requireNonNull(get(type), "Couldn't get any attached data for '" + type.name() + "'");
    }

    @Override
    public boolean contains(AttachmentType<?> type) {
        return attachedData.containsKey(type);
    }

    @Override
    public @UnmodifiableView Map<AttachmentType<?>, Object> getAttachments() {
        return attachedDataView;
    }
}
