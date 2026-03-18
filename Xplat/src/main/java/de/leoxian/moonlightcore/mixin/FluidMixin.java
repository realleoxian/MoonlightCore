package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.api.transfer.fluid.FluidResource;
import de.leoxian.moonlightcore.impl.transfer.fluid.FluidResourceCache;
import de.leoxian.moonlightcore.impl.transfer.fluid.FluidResourceImpl;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Fluid.class)
public class FluidMixin implements FluidResourceCache {
    @Unique
    private @Nullable FluidResource moonlightcore$cachedFluidResource = null;

    @Override
    public FluidResource moonlightcore$getCachedFluidResource() {
        if(moonlightcore$cachedFluidResource == null) {
            moonlightcore$cachedFluidResource = new FluidResourceImpl((Fluid) (Object) this, null);
        }

        return moonlightcore$cachedFluidResource;
    }
}
