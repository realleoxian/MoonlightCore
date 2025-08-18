package de.leowgc.moonlightcore.transfer;

import de.leowgc.moonlightcore.api.transfer.Storage;
import de.leowgc.moonlightcore.api.transfer.Transaction;
import de.leowgc.moonlightcore.api.transfer.TransferResource;
import de.leowgc.moonlightcore.api.transfer.item.ItemResource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class VanillaContainerWrapper implements Storage<ItemStack> {
    private final Container container;

    public VanillaContainerWrapper(Container container) {
        this.container = container;
    }

    @Override
    public int insert(Transaction transaction, TransferResource<ItemStack> resource) {
        if(!this.supportsInsertion() || resource.isBlank()) {
            return 0;
        }
        ItemStack toInsert = resource.get();
        int inserted = 0;

        for(int slot = 0; slot < this.container.getContainerSize(); slot++) {
           ItemStack existing = container.getItem(slot);

           int finalSlot = slot;
           if(existing.isEmpty()) {
               int moveCount = Math.min(toInsert.getCount(), toInsert.getMaxStackSize());
                ItemStack newStack = toInsert.copyWithCount(moveCount);

               transaction.addCloseCallback((state) -> {
                    if(state == Transaction.State.COMMITTED) {
                        this.container.setItem(finalSlot, newStack);
                    }
                });

               inserted += moveCount;
               toInsert.shrink(moveCount);

               if(toInsert.isEmpty()) {
                   break;
               }
           } else if(ItemStack.isSameItemSameTags(existing, toInsert)) {
               int space = existing.getMaxStackSize() - existing.getCount();

               if(space > 0) {
                   int moveCount = Math.min(space, toInsert.getCount());
                   ItemStack newStack = existing.copy();
                   newStack.grow(moveCount);


                   transaction.addCloseCallback((state) -> {
                       if(state == Transaction.State.COMMITTED) {
                           container.setItem(finalSlot, newStack);
                       }
                   });

                   inserted += moveCount;
                   toInsert.shrink(moveCount);

                   if(toInsert.isEmpty()) {
                       break;
                   }
               }
           }
        }

        return inserted;
    }

    @Override
    public TransferResource<ItemStack> extract(Transaction transaction, ItemStack resourceType, int maxAmount) {
        if(!this.supportsExtraction() || resourceType.isEmpty()) {
            return ItemResource.empty();
        }

        for(int slot = 0; slot < this.container.getContainerSize(); slot++) {
            ItemStack existing = container.getItem(slot);

            if(!existing.isEmpty() && ItemStack.isSameItemSameTags(existing, resourceType)) {
                int extractedCount = Math.min(maxAmount, existing.getCount());
                ItemStack extracted = existing.copyWithCount(extractedCount);

                int finalSlot = slot;
                transaction.addCloseCallback((state -> {
                    if(state == Transaction.State.COMMITTED) {
                        existing.shrink(extractedCount);
                        container.setItem(finalSlot, existing.isEmpty() ? ItemStack.EMPTY : existing);
                    }
                }));

                return ItemResource.of(extracted);
            }
        }

        return ItemResource.empty();
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();

        ListTag itemsListTag = new ListTag();
        for(int slot = 0; slot < this.container.getContainerSize(); slot++) {
            ItemStack existing = this.container.getItem(slot);

            if(!existing.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();

                itemTag.putInt("slot", slot);
                itemTag.put("value", existing.save(new CompoundTag()));

                itemsListTag.add(itemTag);
            }
        }
        nbt.put("items", itemsListTag);

        return nbt;
    }

    @Override
    public void fromNBT(CompoundTag nbt) {
        if(nbt.contains("items")) {
            ListTag itemsTag = nbt.getList("items", Tag.TAG_COMPOUND);

            for(int i = 0; i < itemsTag.size(); i++) {
                CompoundTag itemTag = itemsTag.getCompound(i);

                int slot = itemTag.getInt("slot");
                ItemStack value = ItemStack.of(itemTag.getCompound("value"));
                this.container.setItem(slot, value);
            }
        }
    }
}
