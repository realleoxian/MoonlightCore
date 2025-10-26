package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.attachment.AttachmentHolderImpl;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Level.class)
public abstract class LevelMixin implements AttachmentHolderImpl {
    @Shadow
    public abstract boolean isClientSide();

    @Override
    public boolean mlcore_shouldSync() {
        return !this.isClientSide();
    }
}
