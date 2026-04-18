package de.realleoxian.moonlightcore.mixin;

import de.realleoxian.moonlightcore.api.attachment.AttachmentHolder;
import de.realleoxian.moonlightcore.api.attachment.AttachmentMap;
import de.realleoxian.moonlightcore.api.attachment.AttachmentsHolderInfo;
import de.realleoxian.moonlightcore.impl.attachment.AttachmentMapImpl;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements AttachmentHolder {
    @Unique private @Nullable AttachmentMap moonlighcore$attachmentsMap = null;

    @Override
    public AttachmentMap getAttachmentsMap() {
        if(moonlighcore$attachmentsMap == null) {
            moonlighcore$attachmentsMap = AttachmentMapImpl.create(new AttachmentsHolderInfo.EntityHolderInfo(((Entity) (Object) this).getId()));
        }

        return moonlighcore$attachmentsMap;
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    public void moonlightcore$save(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir) {
        if(moonlighcore$attachmentsMap != null) {
            moonlighcore$attachmentsMap.readFromNBT(compound);
        }
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    public void moonlightcore$load(CompoundTag compound, CallbackInfo ci) {
        if(moonlighcore$attachmentsMap != null) {
            moonlighcore$attachmentsMap.writeToNBT(compound);
        }
    }

    @Inject(method = "remove", at = @At("RETURN"))
    public void moonlightCore$onRemove(Entity.RemovalReason reason, CallbackInfo ci) {
        moonlighcore$attachmentsMap = null;
    }
}
