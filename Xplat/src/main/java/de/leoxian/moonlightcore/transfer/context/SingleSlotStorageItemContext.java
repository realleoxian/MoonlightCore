package de.leoxian.moonlightcore.transfer.context;

import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SingleSlotStorageItemContext implements ItemStorageContext {
    private final SingleSlotStorage<ItemResource> slot;

    public SingleSlotStorageItemContext(SingleSlotStorage<ItemResource> slot) {
        this.slot = Objects.requireNonNull(slot, "SingleSlotStorage may not be null");
    }

    @Override
    public SingleSlotStorage<ItemResource> getMainSlot() {
        return this.slot;
    }

    @Override
    public int insertOverflow(Transaction tx, ItemResource resource, int amount) {
        return 0;
    }

    @Override
    public @UnmodifiableView List<SingleSlotStorage<ItemResource>> getAdditionalSlots() {
        return Collections.emptyList();
    }
}
