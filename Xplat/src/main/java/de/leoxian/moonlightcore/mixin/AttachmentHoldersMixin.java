package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.attachment.AttachmentHolderImpl;
import de.leoxian.moonlightcore.attachment.AttachmentInternals;
import de.leoxian.moonlightcore.attachment.AttachmentType;
import de.leoxian.moonlightcore.attachment.sync.AttachmentChange;
import de.leoxian.moonlightcore.core.network.clientbound.S2CAttachmentSyncPacket;
import de.leoxian.moonlightcore.util.MoonlightRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Mixin({BlockEntity.class, Entity.class, ChunkAccess.class, Level.class})
public class AttachmentHoldersMixin implements AttachmentHolderImpl {
    @Unique @Nullable
    private IdentityHashMap<AttachmentType<?>, Object> mlcore_attachments = null;
    @Unique @Nullable
    private IdentityHashMap<AttachmentType<?>, AttachmentChange> mlcore_attachment_changes = null;

    @Override
    public <T> void syncAttachedData(AttachmentType<T> type) {
        if(this.mlcore_attachment_changes == null || !this.mlcore_shouldSync()) {
            return;
        }

        AttachmentChange change = this.mlcore_attachment_changes.get(type);

        if(change != null) {
            this.mlcore_sendChangePacket(type, new S2CAttachmentSyncPacket(List.of(change)));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @Nullable T getAttachedData(AttachmentType<T> type) {
        this.mlcore_validateAttachment(type);
        return this.mlcore_attachments == null ? null : (T) this.mlcore_attachments.get(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @Nullable T setAttachedData(AttachmentType<T> type, @Nullable T value) {
        Objects.requireNonNull(type, "Can't associate data to a null attachment");
        this.mlcore_validateAttachment(type);

        T oldValue;
        if(value == null) {
            oldValue =this.mlcore_attachments == null ? null : (T) this.mlcore_attachments.remove(type);
        } else {
            if(this.mlcore_attachments == null) {
                this.mlcore_attachments = new IdentityHashMap<>(4);
            }

            if(this.mlcore_attachments.containsKey(type) && type.isReadOnly()) {
                throw new IllegalArgumentException("Can't change the value of a read-only attachment");
            }

            oldValue = (T) this.mlcore_attachments.put(type, value);
        }

        if(!Objects.equals(oldValue, value)) {
            this.mlcore_markDirty();

            if(type.isSynced()) {
                this.mlcore_validateSyncedAttachmenet(type);

                AttachmentChange change = AttachmentChange.create(this.mlcore_getHolderInfo(), type, value);
                this.mlcore_acknowledgeAttachmentChange(type, change);
            }
        }

        return oldValue;
    }

    @Override
    public boolean hasAttacheData(AttachmentType<?> type) {
        this.mlcore_validateAttachment(type);
        return this.mlcore_attachments != null && this.mlcore_attachments.containsKey(type);
    }

    @Override
    public void mlcore_writePersistentAttachments(CompoundTag nbt) {
        AttachmentInternals.serializePersistentAttachments(nbt, this.mlcore_attachments);
    }

    @Override
    public void mlcore_readPersistentAttachments(CompoundTag nbt) {
        var fromNBT = AttachmentInternals.deserializePersistentAttachments(nbt);

        if(fromNBT == null) {
            return;
        }

        this.mlcore_attachments = fromNBT;

        if(this.mlcore_shouldSync() && this.mlcore_attachments != null) {
            this.mlcore_attachments.forEach((type, val) -> {
                if(type.isSynced()) {
                    mlcore_acknowledgeAttachmentChange(type, AttachmentChange.create(this.mlcore_getHolderInfo(), type, val));
                }
            });
        }
    }

    @Override
    public void mlcore_computeInitialAttachmentChanges(ServerPlayer player, Consumer<AttachmentChange> changeOutput) {
        if(this.mlcore_attachment_changes == null) {
            return;
        }

        for(var entry : this.mlcore_attachment_changes.entrySet()) {
            if(entry.getKey().syncPredicate().test(this, player)) {
                changeOutput.accept(entry.getValue());
            }
        }
    }

    @Override
    public Map<AttachmentType<?>, Object> mlcore_getAttachments() {
        return this.mlcore_attachments;
    }

    @Unique
    private void mlcore_validateAttachment(AttachmentType<?> type) {
        if(!MoonlightRegistries.ATTACHMENT_TYPE.containsKey(type.id())) {
            throw new IllegalArgumentException("Invalid attachment type, '" + type.id() + "' isn't registered");
        }
    }

    @Unique
    private void mlcore_validateSyncedAttachmenet(AttachmentType<?> type) {
        if(type.isSynced() && !MoonlightRegistries.SYNCED_ATTACHMENT_TYPE.containsKey(type.id())) {
            throw new IllegalArgumentException("Invalid synced attachment type, '" + type.id() + "' isn't registered on the synced registry");
        }
    }

    @Unique
    private void mlcore_acknowledgeAttachmentChange(AttachmentType<?> type, @Nullable AttachmentChange change) {
        if(change == null) {
            if(this.mlcore_attachment_changes == null) {
                return;
            }

            this.mlcore_attachment_changes.remove(type);
        } else {
            if(this.mlcore_attachment_changes == null) {
                this.mlcore_attachment_changes = new IdentityHashMap<>(4);
            }

            this.mlcore_attachment_changes.put(type, change);
        }
    }
}
