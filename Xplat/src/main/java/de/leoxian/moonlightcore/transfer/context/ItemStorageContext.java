package de.leoxian.moonlightcore.transfer.context;

import de.leoxian.moonlightcore.lookup.item.ItemApiLookup;
import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.StorageInternals;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface ItemStorageContext {

    static ItemStorageContext forPlayerInteraction(Player player, InteractionHand hand) {
        if(player.isCreative()) {
            return forCreativeInteraction(player, player.getItemInHand(hand));
        }

        return ofPlayerHand(player, hand);
    }

    static ItemStorageContext forCreativeInteraction(Player player, ItemStack interactingStack) {
        return new CreativeInventoryStorageItemContext(ItemResource.of(interactingStack), interactingStack.getCount(), player);
    }

    static ItemStorageContext ofPlayerHand(Player player, InteractionHand hand) {
        return new InventoryStorageItemContext(player, hand);
    }

    static ItemStorageContext ofPlayerSlot(Player player, SingleSlotStorage<Item, ItemResource> slot) {
        return new InventoryStorageItemContext(player, slot);
    }

    static ItemStorageContext ofSingleSlot(SingleSlotStorage<Item, ItemResource> slot) {
        return new SingleSlotStorageItemContext(slot);
    }

    static ItemStorageContext withConstant(ItemResource resource, int amount) {
        StorageInternals.checkNonNegative(amount);
        return new ConstantStorageItemContext(resource, amount);
    }

    static ItemStorageContext withConstant(ItemStack stack) {
        return withConstant(ItemResource.of(stack), stack.getCount());
    }

    SingleSlotStorage<Item, ItemResource> getMainSlot();

    @UnmodifiableView
    List<SingleSlotStorage<Item, ItemResource>> getAdditionalSlots();

    int insertOverflow(Transaction tx, ItemResource resource, int amount);

    default <A> A find(ItemApiLookup<A, ItemStorageContext> lookup) {
        return resource().isEmpty() ? null : lookup.find(resource().toStack(), this);
    }

    default int exchange(Transaction tx, ItemResource newResource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(newResource, amount);


        try(Transaction nested = Transaction.open(tx)) {
            int extracted = extract(tx, resource(), amount);

            if(insert(tx, newResource, amount) == extracted) {
                nested.commit();
                return extracted;
            }
        }

        return 0;
    }

    default int insert(Transaction tx, ItemResource resource, int amount) {
        int mainInserted = getMainSlot().insert(tx, resource, amount);
        int overflowInserted = insertOverflow(tx, resource, amount - mainInserted);

        return mainInserted + overflowInserted;
    }

    default int extract(Transaction tx, ItemResource resource, int amount) {
        return getMainSlot().extract(tx, resource, amount);
    }

    default ItemResource resource() {
        return getMainSlot().resource();
    }

    default int amount() {
        if(this.resource().isEmpty()) {
            throw new IllegalArgumentException("Amount may not be queried when the current item resource is empty");
        }

        return getMainSlot().amount();
    }

}
