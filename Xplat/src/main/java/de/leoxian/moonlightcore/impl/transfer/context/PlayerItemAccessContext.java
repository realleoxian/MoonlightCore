package de.leoxian.moonlightcore.impl.transfer.context;

import de.leoxian.moonlightcore.api.transfer.context.ItemAccessContext;
import de.leoxian.moonlightcore.api.transfer.item.ItemResource;
import de.leoxian.moonlightcore.api.transfer.item.PlayerInventoryWrapper;
import de.leoxian.moonlightcore.api.transfer.storage.Storage;
import de.leoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.impl.transfer.StoragePreconditions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class PlayerItemAccessContext implements ItemAccessContext {
    private final PlayerInventoryWrapper wrapper;
    private final Storage<ItemResource> slot;

    public PlayerItemAccessContext(Player player, InteractionHand hand) {
        this.wrapper = PlayerInventoryWrapper.of(player);
        this.slot = wrapper.getHandSlot(hand);
    }

    public PlayerItemAccessContext(Player player, Storage<ItemResource> slot) {
        this.wrapper = PlayerInventoryWrapper.of(player);
        this.slot = slot;
    }

    public PlayerItemAccessContext(PlayerInventoryWrapper wrapper, Storage<ItemResource> slot) {
        this.wrapper = wrapper;
        this.slot = slot;
    }

    @Override
    public int insert(TransactionContext tx, ItemResource resource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        int inserted = slot.insert(tx, 0, resource, maxAmount);
        if (maxAmount > inserted) {
            wrapper.placeItemBackInInventory(tx, resource, maxAmount - inserted);
        }

        return maxAmount;
    }

    @Override
    public int extract(TransactionContext tx, ItemResource resource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        return slot.extract(tx, 0, resource, maxAmount);
    }

    @Override
    public ItemResource getResource() {
        return slot.getResource(0);
    }

    @Override
    public int getAmount() {
        return slot.getAmount(0);
    }
}
