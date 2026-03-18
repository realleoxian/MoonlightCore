package de.leoxian.moonlightcore.api.transfer.context;

import de.leoxian.moonlightcore.api.apilookup.ItemApiLookup;
import de.leoxian.moonlightcore.api.transfer.item.CursorSlotWrapper;
import de.leoxian.moonlightcore.api.transfer.item.ItemResource;
import de.leoxian.moonlightcore.api.transfer.item.PlayerInventoryWrapper;
import de.leoxian.moonlightcore.api.transfer.storage.RangedStorage;
import de.leoxian.moonlightcore.api.transfer.transaction.Transaction;
import de.leoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.impl.transfer.StoragePreconditions;
import de.leoxian.moonlightcore.impl.transfer.context.OneByOneItemAccessContext;
import de.leoxian.moonlightcore.impl.transfer.context.PlayerItemAccessContext;
import de.leoxian.moonlightcore.impl.transfer.context.StackItemAccessContext;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public interface ItemAccessContext {

    static ItemAccessContext forPlayerInteraction(Player player, InteractionHand hand) {
        return new PlayerItemAccessContext(player, hand);
    }

    static ItemAccessContext forPlayerCursor(Player player, AbstractContainerMenu containerMenu) {
        return new PlayerItemAccessContext(player, CursorSlotWrapper.get(containerMenu));
    }

    static ItemAccessContext forPlayerSlot(Player player, int slot) {
        PlayerInventoryWrapper inventoryWrapper = PlayerInventoryWrapper.of(player);
        return new PlayerItemAccessContext(inventoryWrapper, new RangedStorage<>(() -> inventoryWrapper, slot, slot + 1));
    }

    static ItemAccessContext forStack(ItemStack stack) {
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("Expected stack to be non-empty");
        }

        return new StackItemAccessContext(stack);
    }

    int insert(TransactionContext tx, ItemResource resource, int maxAmount);

    int extract(TransactionContext tx, ItemResource resource, int maxAmount);

    default int exchange(TransactionContext tx, ItemResource resource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        ItemResource currentResource = getResource();
        StoragePreconditions.notBlank(currentResource);

        try(Transaction nested = tx.openNested()) {
            int extracted = extract(nested, resource, maxAmount);

            if(extracted > 0 && insert(nested, resource, extracted) == extracted) {
                nested.commit();
                return extracted;
            }
        }

        return 0;
    }

    default <A> @Nullable A find(ItemApiLookup<A, ItemAccessContext> lookup) {
        ItemResource resource = getResource();
        return resource.isBlank() ? null : lookup.find(resource.toStack(), this);
    }

    default ItemAccessContext oneByOne() {
        return new OneByOneItemAccessContext(this);
    }

    ItemResource getResource();

    int getAmount();

}
