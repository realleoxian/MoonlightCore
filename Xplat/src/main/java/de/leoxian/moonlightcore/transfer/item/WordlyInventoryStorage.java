package de.leoxian.moonlightcore.transfer.item;

import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.Item;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class WordlyInventoryStorage extends VanillaContainerWrapper {
    private static List<SingleSlotStorage<Item, ItemResource>> createWrapperList(VanillaContainerWrapper storage, Direction direction) {
        WorldlyContainer container = (WorldlyContainer) storage.container;
        int[] availableSlots = container.getSlotsForFace(direction);
        WordlySlotWrapper[] slots = new WordlySlotWrapper[availableSlots.length];

        for(int i = 0; i < availableSlots.length; i++) {
            slots[i] = new WordlySlotWrapper(storage.slotWrappers.get(availableSlots[i]), container, direction);
        }

        return Arrays.asList(slots);
    }

    WordlyInventoryStorage(VanillaContainerWrapper storage, Direction direction) {
        super(Collections.unmodifiableList(createWrapperList(storage, direction)), storage.container);
    }

    static class WordlySlotWrapper implements SingleSlotStorage<Item, ItemResource> {
        private final VanillaContainerWrapper.SlotWrapper wrapper;
        private final WorldlyContainer container;
        private final Direction direction;

        WordlySlotWrapper(VanillaContainerWrapper.SlotWrapper wrapper, WorldlyContainer container, Direction direction) {
            this.wrapper = wrapper;
            this.container = container;
            this.direction = direction;
        }

        @Override
        public int insert(Transaction tx, ItemResource resource, int amount) {
            if(!this.isResourceValid(resource)) {
                return 0;
            }

            return this.wrapper.insert(tx, resource, amount);
        }

        @Override
        public int extract(Transaction tx, ItemResource resource, int amount) {
            if(!this.isResourceValid(resource)) {
                return 0;
            }

            return this.wrapper.extract(tx, resource, amount);
        }

        @Override
        public boolean isResourceValid(ItemResource resource) {
            return this.wrapper.isResourceValid(resource) && this.container.canPlaceItemThroughFace(this.wrapper.slot, resource.getCachedStack(), this.direction);
        }

        @Override
        public int getCapacity(ItemResource resource) {
            return this.wrapper.getCapacity(resource);
        }

        @Override
        public ItemResource resource() {
            return this.wrapper.resource();
        }

        @Override
        public int amount() {
            return this.wrapper.amount();
        }
    }
}
