package de.leoxian.moonlightcore.api.transfer.item;

import de.leoxian.moonlightcore.api.transfer.StorageUtil;
import de.leoxian.moonlightcore.api.transfer.storage.CombinedStorage;
import de.leoxian.moonlightcore.api.transfer.storage.RangedStorage;
import de.leoxian.moonlightcore.api.transfer.storage.Storage;
import de.leoxian.moonlightcore.api.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.impl.transfer.StoragePreconditions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public final class PlayerInventoryWrapper extends VanillaContainerWrapper {

    public static PlayerInventoryWrapper of(Player player) {
        return (PlayerInventoryWrapper) VanillaContainerWrapper.of(player.getInventory());
    }

    private final DroppedItemsJournal droppedItems = new DroppedItemsJournal();
    private final Inventory inventory;

    PlayerInventoryWrapper(Inventory container) {
        super(container);
        this.inventory = container;
    }

    @Override
    public int insert(TransactionContext tx, int index, ItemResource resource, int maxAmount) {
        Objects.checkIndex(index, size());
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        int inserted = 0;
        for(InteractionHand hand : InteractionHand.values()) {
            Storage<ItemResource> handSlot = getHandSlot(hand);

            if(handSlot.getResource(0) == resource) {
                inserted += handSlot.insert(tx, resource, maxAmount - inserted);

                if(inserted == maxAmount) {
                    return inserted;
                }
            }
        }
        inserted += StorageUtil.insertStacking(tx, getMainSlots(), resource, maxAmount - inserted);
        return inserted;
    }

    public void placeItemBackInInventory(TransactionContext tx, ItemResource resource, int maxAmount) {
        int inserted = insert(tx, resource, maxAmount);

        if (inserted < maxAmount) {
            drop(tx, resource, maxAmount - inserted);
        }
    }

    public void drop(TransactionContext tx, ItemResource resource, int maxAmount, boolean randomlyDrop, boolean includeThrowerName) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        if (maxAmount > 0 && !inventory.player.level().isClientSide()) {
            droppedItems.addDrop(tx, resource, maxAmount, randomlyDrop, includeThrowerName);
        }
    }

    public void drop(TransactionContext tx, ItemResource resource, int maxAmount, boolean randomlyDrop) {
        drop(tx, resource, maxAmount, randomlyDrop, false);
    }

    public void drop(TransactionContext tx, ItemResource resource, int maxAmount) {
        drop(tx, resource, maxAmount, false, false);
    }

    public Storage<ItemResource> getSlot(int slot) {
        return getSlotWrapper(slot);
    }

    public Storage<ItemResource> getMainHandSlot() {
        if (Inventory.isHotbarSlot(inventory.selected)) {
            return getSlot(inventory.selected);
        }

        throw new RuntimeException("Unexpected player selected slot: " + inventory.selected);
    }

    public Storage<ItemResource> getHandSlots() {
        return new CombinedStorage<>(getMainHandSlot(), getHandSlot(InteractionHand.OFF_HAND));
    }

    public Storage<ItemResource> getHandSlot(InteractionHand hand) {
        return switch (hand) {
            case MAIN_HAND -> getMainHandSlot();
            case OFF_HAND -> getSlot(Inventory.SLOT_OFFHAND);
        };
    }

    public Storage<ItemResource> getMainSlots() {
        return new RangedStorage<>(() -> this, 0, Inventory.INVENTORY_SIZE);
    }

    private class DroppedItemsJournal extends SnapshotJournal<Integer> {
        private final Deque<DropInfo> entries = new ArrayDeque<>();

        void addDrop(TransactionContext tx, ItemResource resource, int amount, boolean randomlyDrop, boolean includeThrowerName) {
            updateSnapshots(tx);
            entries.add(new DropInfo(resource, amount, randomlyDrop, includeThrowerName));
        }

        @Override
        public Integer createSnapshot() {
            return entries.size();
        }

        @Override
        public void revertToSnapshot(Integer snapshot) {
            while (entries.size() > snapshot) {
                entries.removeLast();
            }
        }

        @Override
        public void onRootCommit(Integer originalState) {
            while(!entries.isEmpty()) {
                DropInfo dropInfo = entries.removeFirst();
                int remainder = dropInfo.amount;

                int maxStackSize = dropInfo.resource.get().getMaxStackSize();
                while(remainder > 0) {
                    int dropped = Math.min(maxStackSize, remainder);
                    inventory.player.drop(dropInfo.resource.toStack(dropped), dropInfo.randomlyDrop, dropInfo.includeThrowerName);

                    remainder -= dropped;
                }
            }
        }

        record DropInfo(ItemResource resource, int amount, boolean randomlyDrop, boolean includeThrowerName) {}
    }
}
