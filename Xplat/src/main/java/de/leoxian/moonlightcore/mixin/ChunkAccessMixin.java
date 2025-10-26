package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.attachment.AttachmentHolderImpl;
import de.leoxian.moonlightcore.attachment.sync.AttachmentHolderInfo;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkAccess.class)
public abstract class ChunkAccessMixin implements AttachmentHolderImpl {
    @Shadow
    public abstract ChunkPos getPos();

    @Shadow
    public abstract void setUnsaved(boolean p_62094_);

    @Override
    public void mlcore_markDirty() {
        this.setUnsaved(true);
    }

    @Override
    public AttachmentHolderInfo<?> mlcore_getHolderInfo() {
        return new AttachmentHolderInfo.ChunkAccessInfo(this.getPos());
    }

    @Override
    public boolean mlcore_shouldSync() {
        return false;
    }
}
