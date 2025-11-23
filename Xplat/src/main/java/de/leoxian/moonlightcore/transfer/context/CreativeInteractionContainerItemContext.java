package de.leoxian.moonlightcore.transfer.context;

import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.StoragePreconditions;
import de.leoxian.moonlightcore.transfer.item.InventoryWrapper;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import net.minecraft.world.entity.player.Player;

public class CreativeInteractionContainerItemContext extends ConstantContainerItemContext {
    private final InventoryWrapper inventoryWrapper;

    public CreativeInteractionContainerItemContext(ItemResource initialResource, int initialAmount, Player player) {
        super(initialResource, initialAmount);
        this.inventoryWrapper = InventoryWrapper.of(player);
    }

    @Override
    public int insertOverflow(TransactionContext context, ItemResource resource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        if(maxAmount > 0) {
            boolean hasItem = false;

            for(SingleSlotStorage<ItemResource> slot : this.inventoryWrapper.getSlots()) {
                if(slot.getResource().equals(resource) && slot.getAmount() > 0) {
                    hasItem = true;
                    break;
                }
            }

            if(!hasItem) {
                inventoryWrapper.offer(context, resource, 1);
            }
        }

        return maxAmount;
    }

    @Override
    public String toString() {
        return "CreativeInteractionContainerItemContext[%d %s]".formatted(getMainSlot().getAmount(), getMainSlot().getResource());
    }
}
