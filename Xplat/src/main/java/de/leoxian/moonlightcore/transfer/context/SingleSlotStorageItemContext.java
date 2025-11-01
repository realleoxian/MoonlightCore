package de.leoxian.moonlightcore.transfer.context;

import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SingleSlotStorageItemContext implements ItemStorageContext {
    private final SingleSlotStorage<Item, ItemResource> slot;

    public SingleSlotStorageItemContext(SingleSlotStorage<Item, ItemResource> slot) {
        this.slot = Objects.requireNonNull(slot, "SingleSlotStorage may not be null");
    }

    @Override
    public SingleSlotStorage<Item, ItemResource> getMainSlot() {
        return this.slot;
    }

    @Override
    public int insertOverflow(Transaction tx, ItemResource resource, int amount) {
        return 0;
    }

    @Override
    public @UnmodifiableView List<SingleSlotStorage<Item, ItemResource>> getAdditionalSlots() {
        return Collections.emptyList();
    }
}
