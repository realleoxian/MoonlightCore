package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.attachment.AttachmentHolder;
import de.leoxian.moonlightcore.attachment.AttachmentHolderImpl;
import de.leoxian.moonlightcore.attachment.AttachmentType;
import de.leoxian.moonlightcore.attachment.sync.AttachmentChange;
import de.leoxian.moonlightcore.core.network.clientbound.S2CAttachmentSyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(ImposterProtoChunk.class)
public class ImposterProtoChunkMixin implements AttachmentHolderImpl {
    @Shadow
    @Final
    private LevelChunk wrapped;

    @Override
    public <T> void syncAttachedData(AttachmentType<T> type) {
        ((AttachmentHolder) this.wrapped).syncAttachedData(type);
    }

    @Override
    public <T> @Nullable T getAttachedData(AttachmentType<T> type) {
        return ((AttachmentHolder) this.wrapped).getAttachedData(type);
    }

    @Override
    public <T> @Nullable T setAttachedData(AttachmentType<T> type, @Nullable T value) {
        return ((AttachmentHolder) this.wrapped).setAttachedData(type, value);
    }

    @Override
    public boolean hasAttacheData(AttachmentType<?> type) {
        return ((AttachmentHolder) this.wrapped).hasAttacheData(type);
    }

    @Override
    public void mlcore_writePersistentAttachments(CompoundTag nbt) {
        ((AttachmentHolderImpl) this.wrapped).mlcore_writePersistentAttachments(nbt);
    }

    @Override
    public void mlcore_readPersistentAttachments(CompoundTag nbt) {
        ((AttachmentHolderImpl) this.wrapped).mlcore_readPersistentAttachments(nbt);
    }

    @Override
    public boolean mlcore_shouldSync() {
        return ((AttachmentHolderImpl) this.wrapped).mlcore_shouldSync();
    }

    @Override
    public void mlcore_computeInitialAttachmentChanges(ServerPlayer player, Consumer<AttachmentChange> changeOutput) {
        ((AttachmentHolderImpl) this.wrapped).mlcore_computeInitialAttachmentChanges(player, changeOutput);
    }

    @Override
    public void mlcore_sendChangePacket(AttachmentType<?> type, S2CAttachmentSyncPacket packet) {
        ((AttachmentHolderImpl) this.wrapped).mlcore_sendChangePacket(type, packet);
    }

    @Override
    public void mlcore_markDirty() {
        ((AttachmentHolderImpl) this.wrapped).mlcore_markDirty();
    }
}
