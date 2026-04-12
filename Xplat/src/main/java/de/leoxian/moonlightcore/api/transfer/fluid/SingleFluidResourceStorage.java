package de.leoxian.moonlightcore.api.transfer.fluid;

import de.leoxian.moonlightcore.api.transfer.storage.SingleResourceStorage;
import de.leoxian.moonlightcore.api.misc.NBTUtils;
import net.minecraft.nbt.CompoundTag;

public abstract class SingleFluidResourceStorage extends SingleResourceStorage<FluidResource> {

    public SingleFluidResourceStorage() {
        super(FluidResource.blank());
    }

    @Override
    public void loadFromNBT(CompoundTag nbt) {
        currentResource = NBTUtils.readOrThrow(nbt, TAG_RESOURCE, FluidResource.CODEC);
        currentAmount = nbt.getInt(TAG_AMOUNT);
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        NBTUtils.store(nbt, TAG_RESOURCE, FluidResource.CODEC, currentResource);
        nbt.putInt(TAG_AMOUNT, currentAmount);
    }

}
