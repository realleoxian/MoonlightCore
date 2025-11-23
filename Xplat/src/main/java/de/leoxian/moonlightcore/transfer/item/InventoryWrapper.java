package de.leoxian.moonlightcore.transfer.item;

import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.StoragePreconditions;
import de.leoxian.moonlightcore.transfer.StorageUtils;
import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public class InventoryWrapper extends VanillaContainerWrapper {

    public static InventoryWrapper of(Player player) {
        return of(player.getInventory());
    }

    public static InventoryWrapper of(Inventory inventory) {
        return of(inventory, null);
    }

    public static InventoryWrapper of(Inventory inventory, @Nullable Direction direction) {
        return (InventoryWrapper) VanillaContainerWrapper.of(inventory, direction);
    }

    private final DroppedItems droppedItems = new DroppedItems();
    private final Inventory inventory;

    InventoryWrapper(Inventory inventory) {
        super(inventory);
        this.inventory = inventory;
    }

    @Override
    public int insert(TransactionContext context, ItemResource insertedResource, int maxAmount) {
        return offer(context, insertedResource, maxAmount);
    }

    public void drop(TransactionContext context, ItemResource resource, int maxAmount, boolean dropAround, boolean includeThrowerName) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        if(maxAmount > 0 && !inventory.player.level().isClientSide()) {
            droppedItems.addDrop(context, resource, maxAmount, dropAround, includeThrowerName);
        }
    }

    public void drop(TransactionContext context, ItemResource resource, int maxAmount, boolean includeThrowerName) {
        drop(context, resource, maxAmount, false, includeThrowerName);
    }

    public void drop(TransactionContext context, ItemResource resource, int maxAmount) {
        drop(context, resource, maxAmount, false, false);
    }

    public int offer(TransactionContext context, ItemResource resource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        int remaining = maxAmount;

        List<SingleSlotStorage<ItemResource>> mainSlots = getSlots().subList(0, Inventory.INVENTORY_SIZE);
        for(InteractionHand hand : InteractionHand.values()) {
            SingleSlotStorage<ItemResource> handSlot = getHandSlot(hand);

            if (handSlot.getResource().equals(resource)) {
                remaining -= handSlot.insert(context, resource, maxAmount);

                if(remaining == 0) {
                    return maxAmount;
                }
            }
        }

        remaining -= StorageUtils.insertStacking(context, mainSlots, resource, maxAmount);
        return maxAmount - remaining;
    }

    public void offerOrDrop(TransactionContext context, ItemResource resource, int maxAmount) {
        int offered = offer(context, resource, maxAmount);
        drop(context, resource, maxAmount - offered);
    }

    public SingleSlotStorage<ItemResource> getHandSlot(InteractionHand hand) {
        Objects.requireNonNull(hand, "Interaction hand may not be null");

        if(hand == InteractionHand.MAIN_HAND) {
            if(Inventory.isHotbarSlot(inventory.selected)) {
                return getSlotWrapper(inventory.selected);
            }

            throw new RuntimeException("Unexpected player selected slot: " + inventory.selected);
        }

        return getSlotWrapper(Inventory.SLOT_OFFHAND);
    }

    private class DroppedItems extends SnapshotJournal<Integer> {
        final Deque<DropInfo> entries = new ArrayDeque<>();

        void addDrop(TransactionContext context, ItemResource resource, int amount, boolean dropAround, boolean includeThrowerName) {
            updateSnapshots(context);
            entries.add(new DropInfo(resource, amount, dropAround, includeThrowerName));
        }

        @Override
        public Integer createSnapshot() {
            return this.entries.size();
        }

        @Override
        public void revertToSnapshot(Integer snapshot) {
            int previousSize = snapshot;

            while(entries.size() > previousSize) {
                entries.removeLast();
            }
        }

        @Override
        public void onRootCommit(Integer originalState) {
            while(!entries.isEmpty()) {
                DropInfo dropInfo = entries.removeFirst();
                int remainder = dropInfo.amount;
                int maxStackSize = dropInfo.resource.getResource().getMaxStackSize();

                while(remainder > 0) {
                    int dropped = Math.min(maxStackSize, remainder);
                    inventory.player.drop(dropInfo.resource.toStack(dropped), dropInfo.dropAround, dropInfo.includeThrowerName);
                    remainder -=  dropped;
                }
            }
        }

        private record DropInfo(ItemResource resource, int amount, boolean dropAround, boolean includeThrowerName) {}
    }
}
