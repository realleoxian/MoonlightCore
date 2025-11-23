package de.leoxian.moonlightcore.transfer.context;

import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.item.InventoryWrapper;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public class PlayerContainerItemContext implements ContainerItemContext {
    private final InventoryWrapper inventoryWrapper;
    private final SingleSlotStorage<ItemResource> slot;

    public PlayerContainerItemContext(Player player, InteractionHand interactionHand) {
        this.inventoryWrapper = InventoryWrapper.of(player);
        this.slot = this.inventoryWrapper.getHandSlot(interactionHand);
    }

    public PlayerContainerItemContext(Player player, SingleSlotStorage<ItemResource> slot) {
        this.inventoryWrapper = InventoryWrapper.of(player);
        this.slot = slot;
    }

    @Override
    public int insertOverflow(TransactionContext context, ItemResource resource, int maxAmount) {
        this.inventoryWrapper.offerOrDrop(context, resource, maxAmount);
        return maxAmount;
    }

    @Override
    public SingleSlotStorage<ItemResource> getMainSlot() {
        return this.slot;
    }

    @Override
    public @UnmodifiableView List<SingleSlotStorage<ItemResource>> getAdditionalSlots() {
        return this.inventoryWrapper.getSlots();
    }

    @Override
    public String toString() {
        return "PlayerContainerItemContext[%d %s %s/%s]".formatted(
                slot.getAmount(), slot.getResource(), this.inventoryWrapper, slot
        );
    }
}
