package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.api.attachment.AttachmentHolder;
import de.leoxian.moonlightcore.api.attachment.AttachmentMap;
import de.leoxian.moonlightcore.api.attachment.AttachmentsHolderInfo;
import de.leoxian.moonlightcore.impl.attachment.AttachmentMapImpl;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements AttachmentHolder {
    @Unique
    private @Nullable AttachmentMap moonlighcore$attachmentsMap = null;

    @Override
    public AttachmentMap getAttachmentsMap() {
        if(moonlighcore$attachmentsMap == null) {
            moonlighcore$attachmentsMap = AttachmentMapImpl.create(new AttachmentsHolderInfo.EntityHolderInfo(((Entity) (Object) this).getId()));
        }

        return moonlighcore$attachmentsMap;
    }

    @Inject(method = "saveAdditional", at = @At("RETURN"))
    public void moonlightcore$saveAdditional(CompoundTag tag, CallbackInfo ci) {
        if(moonlighcore$attachmentsMap != null) {
            moonlighcore$attachmentsMap.writeToNBT(tag);
        }
    }

    @Inject(method = "load", at = @At("RETURN"))
    public void moonlightcore$load(CompoundTag tag, CallbackInfo ci) {
        if(moonlighcore$attachmentsMap != null) {
            moonlighcore$attachmentsMap.readFromNBT(tag);
        }
    }
}
