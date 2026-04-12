package de.leoxian.moonlightcore.api.transfer.item;

import de.leoxian.moonlightcore.api.transfer.storage.SingleResourceStorage;
import de.leoxian.moonlightcore.api.misc.NBTUtils;
import net.minecraft.nbt.CompoundTag;

public abstract class SingleItemResourceStorage extends SingleResourceStorage<ItemResource> {

    public SingleItemResourceStorage() {
        super(ItemResource.blank());
    }

    @Override
    public final void loadFromNBT(CompoundTag nbt) {
        currentResource = NBTUtils.readOrGet(nbt, TAG_RESOURCE, ItemResource.CODEC, ItemResource::blank);
        currentAmount = nbt.getInt(TAG_AMOUNT);
    }

    @Override
    public final void writeToNBT(CompoundTag nbt) {
        NBTUtils.store(nbt, TAG_RESOURCE, ItemResource.CODEC, currentResource);
        nbt.putInt(TAG_AMOUNT, currentAmount);
    }

}
