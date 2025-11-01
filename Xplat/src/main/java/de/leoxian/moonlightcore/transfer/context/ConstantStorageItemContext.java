package de.leoxian.moonlightcore.transfer.context;

import de.leoxian.moonlightcore.transfer.SingleResourceStorage;
import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.StorageInternals;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.List;

public class ConstantStorageItemContext implements ItemStorageContext {
    private final SingleResourceStorage<Item, ItemResource> backingSlot = new SingleResourceStorage<>(ItemResource.empty()) {
        @Override
        public int insert(Transaction tx, ItemResource resource, int amount) {
            StorageInternals.checkNonEmptyNonNegative(resource, amount);
            return 0;
        }

        @Override
        public int extract(Transaction tx, ItemResource resource, int amount) {
            StorageInternals.checkNonEmptyNonNegative(resource, amount);
            return amount;
        }

        @Override
        public int getCapacity(ItemResource resource) {
            return Integer.MAX_VALUE;
        }
    };

    public ConstantStorageItemContext(ItemResource resource, int amount) {
        backingSlot.resource = resource;
        backingSlot.amount = amount;
    }

    @Override
    public SingleSlotStorage<Item, ItemResource> getMainSlot() {
        return backingSlot;
    }

    @Override
    public @UnmodifiableView List<SingleSlotStorage<Item, ItemResource>> getAdditionalSlots() {
        return Collections.emptyList();
    }

    @Override
    public int insertOverflow(Transaction tx, ItemResource resource, int amount) {
        return amount;
    }
}
