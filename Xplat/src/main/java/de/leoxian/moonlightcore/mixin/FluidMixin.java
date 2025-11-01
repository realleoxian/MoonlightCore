package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.transfer.TransferResourceExtension;
import de.leoxian.moonlightcore.transfer.fluid.FluidResource;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Fluid.class)
public class FluidMixin implements TransferResourceExtension<FluidResource> {
    @Unique
    private FluidResource mlcore_cachedFluidResource = null;

    @Override
    public FluidResource mlcore_getCachedResource() {
        if(this.mlcore_cachedFluidResource == null) {
            this.mlcore_cachedFluidResource = new FluidResource((Fluid) (Object) this, null);
        }

        return this.mlcore_cachedFluidResource;
    }
}
