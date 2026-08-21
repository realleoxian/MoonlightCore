package de.leoxian.moonlightcore.mixin.transfer;

import de.leoxian.moonlightcore.common.transfer.fluid.FluidResource;
import de.leoxian.moonlightcore.internal.common.transfer.fluid.FluidResourceCache;
import de.leoxian.moonlightcore.internal.common.transfer.fluid.FluidResourceImpl;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Fluid.class)
public class FluidMixin implements FluidResourceCache {
    @Unique
    private FluidResource moonlightcore$cachedFluidResource = null;

    @Override
    public FluidResource moonlightcore$getCachedFluidResource() {
        FluidResource ret = this.moonlightcore$cachedFluidResource;
        if (ret == null) {
            ret = this.moonlightcore$cachedFluidResource = new FluidResourceImpl(
                    (Fluid) (Object) this,
                    DataComponentPatch.EMPTY
            );
        }
        return ret;
    }
}
