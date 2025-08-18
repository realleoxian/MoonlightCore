package de.leowgc.moonlightcore.transfer.item;

import de.leowgc.moonlightcore.api.transfer.Transaction;
import de.leowgc.moonlightcore.api.transfer.TransferResource;
import de.leowgc.moonlightcore.api.transfer.item.ItemResource;
import de.leowgc.moonlightcore.api.transfer.item.ItemStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class SimpleItemStorage implements ItemStorage {

    private ItemStack stored = ItemStack.EMPTY;
    private final int capacity;

    public SimpleItemStorage(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public int insert(Transaction transaction, TransferResource<ItemStack> resource) {
        if(resource.isBlank() || !this.supportsInsertion()) {
            return 0;
        }

        ItemStack incoming = resource.get();
        int maxInsert = Math.min(resource.amount(), capacity - this.stored.getCount());

        if(!stored.isEmpty() && !ItemStack.isSameItemSameTags(stored, incoming)) {
            return 0;
        } else {
            transaction.addCloseCallback(state -> {
                if(state == Transaction.State.COMMITTED) {
                    if(this.stored.isEmpty()) {
                        stored = incoming.copyWithCount(maxInsert);
                    } else {
                        stored.grow(maxInsert);
                    }
                }
            });
        }

        return maxInsert;
    }

    @Override
    public TransferResource<ItemStack> extract(Transaction transaction, ItemStack resourceType, int maxAmount) {
        if(this.stored.isEmpty() || !ItemStack.isSameItemSameTags(this.stored, resourceType) || !this.supportsExtraction()) {
            return ItemResource.empty();
        }

        int amountToExtract = Math.min(maxAmount, this.stored.getCount());
        ItemStack extractedStack = this.stored.copyWithCount(amountToExtract);

        transaction.addCloseCallback((state) -> {
            if(state == Transaction.State.COMMITTED) {
                this.stored.shrink(amountToExtract);

                if(this.stored.isEmpty()) {
                    this.stored = ItemStack.EMPTY;
                }
            }
        });

        return new ItemResourceImpl(extractedStack, amountToExtract);
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();

        if(!this.stored.isEmpty()) {
            nbt.put("value", this.stored.save(new CompoundTag()));
        }

        return nbt;
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
        if(nbt.contains("value")) {
            this.stored = ItemStack.of(nbt.getCompound("value"));
        }
    }

    public ItemStack getStored() {
        return stored.copy();
    }
}
