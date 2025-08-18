package de.leowgc.moonlightcore.transfer.item;

import de.leowgc.moonlightcore.api.transfer.SlottedStorage;
import de.leowgc.moonlightcore.api.transfer.Transaction;
import de.leowgc.moonlightcore.api.transfer.TransferResource;
import de.leowgc.moonlightcore.api.transfer.item.ItemResource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public final class SlottedItemStorage implements SlottedStorage<ItemStack> {
    private final ItemStack[] slots;
    private final int[] slotCapacities;
    @SuppressWarnings("FieldCanBeLocal")
    private final int defaultSlotCapacity;
    private final int slotCount;

    public SlottedItemStorage(int slotCount, int defaultSlotCapacity) {
        this.slotCount = slotCount;
        this.defaultSlotCapacity = defaultSlotCapacity;

        this.slots = new ItemStack[slotCount];
        this.slotCapacities = new int[slotCount];
        Arrays.fill(this.slots, ItemStack.EMPTY);
        Arrays.fill(this.slotCapacities, this.defaultSlotCapacity);
    }

    @Override
    public int insertIntoSlot(Transaction transaction, int slot, TransferResource<ItemStack> resource) {
        if((slot < 0 || slot > this.slotCount) || resource.isBlank() || !this.supportsInsertion()) {
            return 0;
        }

        ItemStack current = this.slots[slot];
        ItemStack toInsert = resource.get();
        int maxInsert = Math.min(resource.amount(), this.slotCapacities[slot] - current.getCount());

        if((!current.isEmpty() && !ItemStack.isSameItemSameTags(current, toInsert)) || maxInsert <= 0) {
            return 0;
        }

        transaction.addCloseCallback((state) -> {
            if(state == Transaction.State.COMMITTED) {
                if(current.isEmpty()) {
                    this.slots[slot] = toInsert.copyWithCount(maxInsert);
                } else {
                    this.slots[slot].grow(maxInsert);
                }
            }
        });

        return maxInsert;
    }

    @Override
    public int insert(Transaction transaction, TransferResource<ItemStack> resource) {
        if(resource.isBlank() || !this.supportsInsertion()) {
            return 0;
        }

        int remaining = resource.amount();
        ItemStack stackToInsert = resource.get();

        for(int slot = 0; slot < this.slotCount && remaining > 0; slot++) {
            ItemStack current = this.slots[slot];

            if(current.isEmpty() || ItemStack.isSameItemSameTags(current, stackToInsert)) {
                int inserted = insertIntoSlot(transaction, slot, new ItemResourceImpl(stackToInsert, remaining));
                remaining -= inserted;
            }
        }

        return resource.amount() - remaining;
    }

    @Override
    public TransferResource<ItemStack> extractFromSlot(Transaction transaction, int slot, ItemStack resourceType, int maxAmount) {
        if(slot < 0 || slot > this.slotCount || !this.supportsExtraction()) {
            return ItemResource.empty();
        }

        ItemStack current = this.slots[slot];
        if(current.isEmpty() || !ItemStack.isSameItemSameTags(current, resourceType)) {
            return ItemResource.empty();
        }

        int toExtract = Math.min(maxAmount, current.getCount());
        ItemStack extracted = current.copyWithCount(toExtract);

        transaction.addCloseCallback((state) -> {
            if(state == Transaction.State.COMMITTED) {
                this.slots[slot].shrink(toExtract);

                if(this.slots[slot].isEmpty()) {
                    slots[slot] = ItemStack.EMPTY;
                }
            }
        });

        return new ItemResourceImpl(extracted, toExtract);
    }

    @Override
    public TransferResource<ItemStack> extract(Transaction transaction, ItemStack resourceType, int maxAmount) {
        if(resourceType.isEmpty() || !this.supportsExtraction()) {
            return ItemResource.empty();
        }

        int remaining = maxAmount;
        ItemStack combinedExtracted = ItemStack.EMPTY;

        for(int slot = 0; slot < this.slotCount && remaining > 0; slot++) {
            if (ItemStack.isSameItemSameTags(slots[slot], resourceType)) {
                TransferResource<ItemStack> extracted = extractFromSlot(transaction, slot, resourceType, remaining);

                if (!extracted.isBlank()) {
                    remaining -= extracted.amount();

                    if (combinedExtracted.isEmpty()) {
                        combinedExtracted = extracted.get().copy();
                    } else {
                        combinedExtracted.grow(extracted.amount());
                    }
                }
            }
        }

        return combinedExtracted.isEmpty() ? ItemResource.empty() : new ItemResourceImpl(combinedExtracted, maxAmount - remaining);
    }

    @Override
    public TransferResource<ItemStack> getResourceInSlot(int slot) {
        if(slot < 0 || slot > this.slotCount) {
            return ItemResource.empty();
        }

        ItemStack stack = this.slots[slot];
        return stack.isEmpty() ? ItemResource.empty() : new ItemResourceImpl(stack, stack.getCount());
    }

    @Override
    public void setSlotCapacity(int slot, int capacity) {
        if(slot < 0 || slot > this.slotCount) {
            throw new IllegalStateException("Tried to set invalid slot capacity: " + slot + " when there is " + this.slotCount + " slots");
        }

        this.slotCapacities[slot] = capacity;
    }

    @Override
    public int getSlotCapacity(int slot) {
        if(slot < 0 || slot > this.slotCount) {
            throw new IllegalStateException("Tried to get invalid slot capacity: " + slot + " when there is " + this.slotCount + " slots");
        }

        return this.slotCapacities[slot];
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();

        ListTag itemsTag = new ListTag();
        for(int slot = 0; slot < this.slotCount; slot++) {
            ItemStack itemInSlot = this.slots[slot];

            if(!itemInSlot.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();

                itemTag.putInt("slot", slot);
                itemTag.put("value", itemInSlot.save(new CompoundTag()));

                itemsTag.add(itemTag);
            }
        }
        nbt.put("items", itemsTag);

        return nbt;
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
        if (nbt.contains("items")) {
            ListTag itemsTag = nbt.getList("items", Tag.TAG_COMPOUND);

            for (int i = 0; i < itemsTag.size(); i++) {
                CompoundTag itemTag = itemsTag.getCompound(i);
                int slot = itemTag.getInt("slot");

                if (slot >= 0 && slot < this.slotCount) {
                    this.slots[slot] = ItemStack.of(itemTag.getCompound("value"));
                }
            }
        }
    }

    @Override
    public int getSlotCount() {
        return this.slotCount;
    }
}
