package de.leoxian.moonlightcore.transfer.fluid;

import de.leoxian.moonlightcore.transfer.ResourceStack;
import de.leoxian.moonlightcore.transfer.SingleResourceStorage;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

public abstract class SingleFluidStorage extends SingleResourceStorage<FluidResource> {

    public static SingleFluidStorage withFixedCapacity(int capacity, Consumer<ResourceStack<FluidResource>> onChange) {
        return new SingleFluidStorage() {
            @Override
            public int getCapacity(FluidResource resource) {
                return capacity;
            }

            @Override
            public void onRootCommit(ResourceStack<FluidResource> originalState) {
                onChange.accept(originalState);
            }
        };
    }

    public SingleFluidStorage() {
        super(FluidResource.blank());
    }

    public void readFromNBT(CompoundTag nbt) {
        this.currentResource = FluidResource.fromNBT(nbt.getCompound("resource"));
        this.amount = nbt.getInt("amount");
    }

}
