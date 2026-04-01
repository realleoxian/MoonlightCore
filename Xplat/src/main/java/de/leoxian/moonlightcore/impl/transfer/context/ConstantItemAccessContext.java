package de.leoxian.moonlightcore.impl.transfer.context;

import de.leoxian.moonlightcore.api.transfer.context.ItemAccessContext;
import de.leoxian.moonlightcore.api.transfer.item.ItemResource;
import de.leoxian.moonlightcore.api.transfer.item.SingleItemResourceStorage;
import de.leoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.impl.transfer.StoragePreconditions;

public class ConstantItemAccessContext implements ItemAccessContext {
    private final SingleItemResourceStorage backingSlot = new SingleItemResourceStorage() {
        @Override
        public int insert(TransactionContext tx, int index, ItemResource resource, int maxAmount) {
            StoragePreconditions.singleSlotIndexCheck(index);
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);

            return 0;
        }

        @Override
        public int extract(TransactionContext tx, int index, ItemResource resource, int maxAmount) {
            StoragePreconditions.singleSlotIndexCheck(index);
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);

            return maxAmount;
        }

        @Override
        public int getCapacity(int index, ItemResource resource) {
            return Integer.MAX_VALUE;
        }
    };

    public ConstantItemAccessContext(ItemResource resource, int amount) {
        this.backingSlot.currentResource = resource;
        this.backingSlot.currentAmount = amount;
    }

    @Override
    public int insert(TransactionContext tx, ItemResource resource, int maxAmount) {
        return backingSlot.insert(tx, 0, resource, maxAmount);
    }

    @Override
    public int extract(TransactionContext tx, ItemResource resource, int maxAmount) {
        return backingSlot.extract(tx, 0, resource, maxAmount);
    }

    @Override
    public ItemResource getResource() {
        return backingSlot.currentResource;
    }

    @Override
    public int getAmount() {
        return backingSlot.currentAmount;
    }
}
