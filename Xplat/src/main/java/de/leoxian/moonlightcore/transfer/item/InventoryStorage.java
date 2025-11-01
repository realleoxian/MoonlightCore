package de.leoxian.moonlightcore.transfer.item;

import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.Storage;
import de.leoxian.moonlightcore.transfer.StorageInternals;
import de.leoxian.moonlightcore.transfer.StorageUtils;
import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class InventoryStorage extends VanillaContainerWrapper {

    public static InventoryStorage of(Player player) {
        return of(player.getInventory());
    }

    public static InventoryStorage of(Inventory inventory) {
        return (InventoryStorage) VanillaContainerWrapper.of(inventory);
    }

    private final DroppedStacks droppedStacks = new DroppedStacks();
    private final Inventory inventory;

    protected InventoryStorage(Inventory inventory) {
        super(inventory);
        this.inventory = inventory;
    }

    public int offer(Transaction tx, ItemResource resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        int initialAmount = amount;
        List<SingleSlotStorage<ItemResource>> mainSlots = this.getSlots().subList(0, Inventory.INVENTORY_SIZE);

        for(InteractionHand hand : InteractionHand.values()) {
            SingleSlotStorage<ItemResource> handSlot = getHandSlot(hand);

            if(handSlot.resource().is(resource.get())) {
                amount -= handSlot.insert(tx, resource, amount);

                if(amount == 0) {
                    return initialAmount;
                }
            }
        }

        amount -= StorageUtils.insertStacking(tx, resource, amount, mainSlots);
        return initialAmount - amount;
    }

    public void drop(Transaction tx, ItemResource resource, int amount, boolean throwRandomly, boolean retainOwnership) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        if(amount > 0 && !inventory.player.level().isClientSide()) {
            droppedStacks.addDrop(tx, resource, amount, throwRandomly, retainOwnership);
        }
    }

    public void drop(Transaction tx, ItemResource resource, int amount, boolean retainOwnership) {
        drop(tx, resource, amount, false, retainOwnership);
    }

    public void drop(Transaction tx, ItemResource resource, int amount) {
        this.drop(tx, resource, amount, false);
    }

    public void offerOrDrop(Transaction tx, ItemResource resource, int amount) {
        int offered = offer(tx, resource, amount);
        drop(tx, resource, amount - offered);
    }

    @Override
    public int insert(Transaction tx, ItemResource resource, int amount) {
        return offer(tx, resource, amount);
    }

    public SingleSlotStorage<ItemResource> getHandSlot(InteractionHand hand) {
        if(hand == InteractionHand.MAIN_HAND) {
            if(Inventory.isHotbarSlot(inventory.selected)) {
                return getSlots().get(inventory.selected);
            } else {
                throw new RuntimeException("Unexpected player selected slot: " + inventory.selected);
            }
        } else if(hand == InteractionHand.OFF_HAND) {
            return getSlots().get(Inventory.SLOT_OFFHAND);
        }

        throw new UnsupportedOperationException("Unknown hand: " + hand);
    }

    private class DroppedStacks extends SnapshotJournal<Integer> {
        private final List<Entry> entries = new ArrayList<>();

        void addDrop(TransactionContext ctx, ItemResource resource, int amount, boolean throwRandomly, boolean retainOwnership) {
            updateSnapshots(ctx);
            entries.add(new Entry(resource, amount, throwRandomly, retainOwnership));
        }

        @Override
        public Integer createSnapshot() {
            return entries.size();
        }

        @Override
        public void revertToSnapshot(Integer snapshot) {
            int previousSize = snapshot;

            while(entries.size() > previousSize) {
                entries.remove(entries.size() - 1);
            }
        }

        @Override
        public void onRootCommit(Integer originalState) {
            for(Entry entry : this.entries) {
                int remainder = entry.amount;

                while(remainder > 0) {
                    int dropped = (int) Math.min(entry.resource.get().getMaxStackSize(), remainder);
                    inventory.player.drop(entry.resource.toStack(dropped), entry.throwRandomly, entry.retainOwnership);
                    remainder -= dropped;
                }
            }

            entries.clear();
        }

        private record Entry(ItemResource resource, int amount, boolean throwRandomly, boolean retainOwnership) {}
    }
}
