package de.leoxian.moonlightcore.transfer.context;

import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.item.InventoryStorage;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public class InventoryStorageItemContext implements ItemStorageContext {
    private final InventoryStorage playerStorage;
    private final SingleSlotStorage<ItemResource> slot;

    public InventoryStorageItemContext(Player player, InteractionHand hand) {
        this.playerStorage = InventoryStorage.of(player);
        this.slot = this.playerStorage.getHandSlot(hand);
    }

    public InventoryStorageItemContext(Player player, SingleSlotStorage<ItemResource> slot) {
        this.playerStorage = InventoryStorage.of(player);
        this.slot = slot;
    }

    @Override
    public int insertOverflow(Transaction tx, ItemResource resource, int amount) {
        this.playerStorage.offerOrDrop(tx, resource, amount);
        return amount;
    }

    @Override
    public @UnmodifiableView List<SingleSlotStorage<ItemResource>> getAdditionalSlots() {
        return this.playerStorage.getSlots();
    }

    @Override
    public SingleSlotStorage<ItemResource> getMainSlot() {
        return this.slot;
    }
}
