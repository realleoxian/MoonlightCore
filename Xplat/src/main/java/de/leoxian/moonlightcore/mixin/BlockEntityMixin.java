package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.attachment.AttachmentHolderImpl;
import de.leoxian.moonlightcore.attachment.AttachmentType;
import de.leoxian.moonlightcore.attachment.sync.AttachmentHolderInfo;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.core.network.clientbound.S2CAttachmentSyncPacket;
import de.leoxian.moonlightcore.util.PlayerTrackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements AttachmentHolderImpl {
    @Shadow
    public abstract void setChanged();

    @Shadow
    public abstract boolean hasLevel();

    @Shadow
    @Nullable
    protected Level level;

    @Shadow
    public abstract BlockPos getBlockPos();

    @Inject(method = "load", at = @At("RETURN"))
    private void mlcore_loadBlockEntityAttachments(CompoundTag tag, CallbackInfo ci) {
        this.mlcore_readPersistentAttachments(tag);
    }

    @Inject(method = "saveWithId", at = @At("TAIL"))
    private void mlcore_saveBlockEntityAttachments(CallbackInfoReturnable<CompoundTag> cir) {
        this.mlcore_readPersistentAttachments(cir.getReturnValue());
    }

    @Override
    public void mlcore_sendChangePacket(AttachmentType<?> type, S2CAttachmentSyncPacket packet) {
        PlayerTrackUtils.tracking((BlockEntity) (Object) this).forEach(player -> {
            if(type.syncPredicate().test(this, player)) {
                MoonlightCore.PACKET_DISPATCHER.sendToPlayer(player, packet);
            }
        });
    }

    @Override
    public void mlcore_markDirty() {
        this.setChanged();
    }

    @Override
    public AttachmentHolderInfo<?> mlcore_getHolderInfo() {
        return new AttachmentHolderInfo.BlockEntityInfo(this.getBlockPos());
    }

    @Override
    public boolean mlcore_shouldSync() {
        return !this.hasLevel() || !this.level.isClientSide();
    }
}
