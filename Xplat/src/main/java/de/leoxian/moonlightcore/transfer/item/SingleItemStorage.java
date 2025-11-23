package de.leoxian.moonlightcore.transfer.item;

import de.leoxian.moonlightcore.transfer.SingleResourceStorage;
import net.minecraft.nbt.CompoundTag;

public abstract class SingleItemStorage extends SingleResourceStorage<ItemResource> {

    public SingleItemStorage() {
        super(ItemResource.blank());
    }

    public void readFromNBT(CompoundTag nbt) {
        this.currentResource = ItemResource.fromNBT(nbt.getCompound("resource"));
        this.amount = nbt.getInt("amount");
    }

}
