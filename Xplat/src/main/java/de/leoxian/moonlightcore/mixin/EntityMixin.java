package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.attachment.AttachmentHolderImpl;
import de.leoxian.moonlightcore.attachment.AttachmentType;
import de.leoxian.moonlightcore.attachment.sync.AttachmentHolderInfo;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.core.network.clientbound.S2CAttachmentSyncPacket;
import de.leoxian.moonlightcore.util.PlayerTrackUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements AttachmentHolderImpl {
    @Shadow
    private int id;

    @Shadow
    public abstract Level level();

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    private void mlcore_loadEntityAttachments(CompoundTag compound, CallbackInfo ci) {
        this.mlcore_readPersistentAttachments(compound);
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    private void mlcore_saveEntityAttachments(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir) {
        this.mlcore_writePersistentAttachments(compound);
    }

    @Override
    public void mlcore_sendChangePacket(AttachmentType<?> type, S2CAttachmentSyncPacket packet) {
        PlayerTrackUtils.tracking((Entity) (Object) this).stream().forEach(player -> {
            if(type.syncPredicate().test(this, player)) {
                MoonlightCore.PACKET_DISPATCHER.sendToPlayer(player, packet);
            }
        });
    }

    @Override
    public AttachmentHolderInfo<?> mlcore_getHolderInfo() {
        return new AttachmentHolderInfo.EntityInfo(this.id);
    }

    @Override
    public boolean mlcore_shouldSync() {
        return !this.level().isClientSide();
    }
}
