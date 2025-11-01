package de.leoxian.moonlightcore.transfer.context;

import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.StorageInternals;
import de.leoxian.moonlightcore.transfer.item.InventoryStorage;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class CreativeInventoryStorageItemContext extends ConstantStorageItemContext {

    private final InventoryStorage playerStorage;

    public CreativeInventoryStorageItemContext(ItemResource resource, int amount, Player player) {
        super(resource, amount);
        this.playerStorage = InventoryStorage.of(player);
    }

    @Override
    public int insertOverflow(Transaction tx, ItemResource resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        if(amount > 0) {
            boolean hasItem = false;

            for(SingleSlotStorage<Item, ItemResource> slot : this.playerStorage.getSlots()) {
                if(slot.resource().is(resource.get()) && slot.amount() > 0) {
                    hasItem = true;
                    break;
                }
            }

            if(!hasItem) {
                this.playerStorage.offer(tx, resource, 1);
            }
        }

        return amount;
    }
}
