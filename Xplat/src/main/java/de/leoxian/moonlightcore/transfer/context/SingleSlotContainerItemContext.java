package de.leoxian.moonlightcore.transfer.context;

import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.List;

public class SingleSlotContainerItemContext implements ContainerItemContext {
    private final SingleSlotStorage<ItemResource> slot;

    public SingleSlotContainerItemContext(SingleSlotStorage<ItemResource> slot) {
        this.slot = slot;
    }

    @Override
    public int insertOverflow(TransactionContext context, ItemResource resource, int maxAmount) {
        return 0;
    }

    @Override
    public SingleSlotStorage<ItemResource> getMainSlot() {
        return this.slot;
    }

    @Override
    public @UnmodifiableView List<SingleSlotStorage<ItemResource>> getAdditionalSlots() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "SingleSlotContainerItemContext[%d %s %s]".formatted(this.slot.getAmount(), slot.getResource(), this.slot);
    }
}
