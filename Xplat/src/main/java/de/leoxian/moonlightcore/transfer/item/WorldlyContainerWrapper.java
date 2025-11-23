package de.leoxian.moonlightcore.transfer.item;

import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class WorldlyContainerWrapper extends VanillaContainerWrapper {
    WorldlyContainerWrapper(VanillaContainerWrapper containerWrapper, Direction direction) {
        super(Collections.unmodifiableList(createWrapperList(containerWrapper, direction)), containerWrapper.container);
    }

    private static List<SingleSlotStorage<ItemResource>> createWrapperList(VanillaContainerWrapper containerWrapper, Direction direction) {
        WorldlyContainer container = (WorldlyContainer) containerWrapper.container;
        int[] availableSlots = container.getSlotsForFace(direction);
        WordlyContainerSlotWrapper[] slots = new WordlyContainerSlotWrapper[availableSlots.length];

        for(int i = 0; i < availableSlots.length; i++) {
            slots[i] = new WordlyContainerSlotWrapper(containerWrapper.getSlotWrapper(availableSlots[i]), container, direction);
        }

        return Arrays.asList(slots);
    }

    private record WordlyContainerSlotWrapper(SlotWrapper slotWrapper, WorldlyContainer worldlyContainer, Direction direction) implements SingleSlotStorage<ItemResource> {
        @Override
        public int insert(TransactionContext context, ItemResource insertedResource, int maxAmount) {
            if (!worldlyContainer.canPlaceItemThroughFace(slotWrapper.index, insertedResource.getCachedStack(), direction)) {
                return 0;
            }

            return slotWrapper.insert(context, insertedResource, maxAmount);
        }

        @Override
        public int extract(TransactionContext context, ItemResource extractedResource, int maxAmount) {
            if (!worldlyContainer.canPlaceItemThroughFace(slotWrapper.index, extractedResource.getCachedStack(), direction)) {
                return 0;
            }

            return slotWrapper.extract(context, extractedResource, maxAmount);
        }

        @Override
        public boolean isResourceBlank() {
            return slotWrapper.isResourceBlank();
        }

        @Override
        public ItemResource getResource() {
            return slotWrapper.getResource();
        }

        @Override
        public int getAmount() {
            return slotWrapper.getAmount();
        }

        @Override
        public int getCapacity(ItemResource resource) {
            return slotWrapper.getCapacity(resource);
        }
    }
}
