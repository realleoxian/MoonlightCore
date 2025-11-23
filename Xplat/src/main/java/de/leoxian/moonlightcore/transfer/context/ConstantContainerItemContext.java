package de.leoxian.moonlightcore.transfer.context;

import de.leoxian.moonlightcore.transfer.SingleResourceStorage;
import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.StoragePreconditions;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.List;

public class ConstantContainerItemContext implements ContainerItemContext {
    private final SingleResourceStorage<ItemResource> backingSlot = new SingleResourceStorage<ItemResource>(ItemResource.blank()) {
        @Override
        public int getCapacity(ItemResource resource) {
            return Integer.MAX_VALUE;
        }

        @Override
        public int insert(TransactionContext context, ItemResource insertedResource, int maxAmount) {
            StoragePreconditions.notBlankNotNegative(insertedResource, maxAmount);
            return 0;
        }

        @Override
        public int extract(TransactionContext context, ItemResource extractedResource, int maxAmount) {
            StoragePreconditions.notBlankNotNegative(extractedResource, maxAmount);
            return maxAmount;
        }
    };

    public ConstantContainerItemContext(ItemResource initialResource, int initialAmount) {
        this.backingSlot.currentResource = initialResource;
        this.backingSlot.amount = initialAmount;
    }

    @Override
    public int insertOverflow(TransactionContext context, ItemResource resource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        return maxAmount;
    }

    @Override
    public SingleSlotStorage<ItemResource> getMainSlot() {
        return this.backingSlot;
    }

    @Override
    public @UnmodifiableView List<SingleSlotStorage<ItemResource>> getAdditionalSlots() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "ConstantContainerItemContext[%d %s]".formatted(getMainSlot().getAmount(), getMainSlot().getResource());
    }
}
