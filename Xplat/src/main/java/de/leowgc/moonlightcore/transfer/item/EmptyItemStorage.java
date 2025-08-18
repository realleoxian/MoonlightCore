package de.leowgc.moonlightcore.transfer.item;

import de.leowgc.moonlightcore.api.transfer.Transaction;
import de.leowgc.moonlightcore.api.transfer.TransferResource;
import de.leowgc.moonlightcore.api.transfer.item.ItemResource;
import de.leowgc.moonlightcore.api.transfer.item.ItemStorage;
import net.minecraft.world.item.ItemStack;

public final class EmptyItemStorage implements ItemStorage {

    @Override
    public int insert(Transaction transaction, TransferResource<ItemStack> resource) {
        return 0;
    }

    @Override
    public TransferResource<ItemStack> extract(Transaction transaction, ItemStack resourceType, int maxAmount) {
        return ItemResource.empty();
    }

    @Override
    public boolean supportsExtraction() {
        return false;
    }

    @Override
    public boolean supportsInsertion() {
        return false;
    }

}
