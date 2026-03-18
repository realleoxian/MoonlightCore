package de.leoxian.moonlightcore.api.transfer.item;

import de.leoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;

class WorldlyContainerWrapper extends VanillaContainerWrapper {
    private final VanillaContainerWrapper containerWrapper;
    private final @Nullable Direction side;

    protected WorldlyContainerWrapper(WorldlyContainer container, VanillaContainerWrapper wrapper, @Nullable Direction side) {
        super(container);
        this.containerWrapper = wrapper;
        this.side = side;
    }

    @Override
    public int insert(TransactionContext tx, int index, ItemResource resource, int maxAmount) {
        int convertedSlot = convertSlot(index);
        if(!((WorldlyContainer) container).canPlaceItemThroughFace(convertedSlot, resource.toStack(), side)) {
            return 0;
        }

        return containerWrapper.insert(tx, convertedSlot, resource, maxAmount);
    }

    @Override
    public int extract(TransactionContext tx, int index, ItemResource resource, int maxAmount) {
        int convertedSlot = convertSlot(index);
        if(!((WorldlyContainer) container).canPlaceItemThroughFace(convertedSlot, resource.toStack(), side)) {
            return 0;
        }

        return containerWrapper.insert(tx, convertedSlot, resource, maxAmount);
    }

    @Override
    public ItemResource getResource(int index) {
        return containerWrapper.getResource(convertSlot(index));
    }

    @Override
    public int getAmount(int index) {
        return containerWrapper.getAmount(convertSlot(index));
    }

    @Override
    public boolean isBlank(int index) {
        return containerWrapper.isBlank(convertSlot(index));
    }

    @Override
    public int size() {
        return side == null ? container.getContainerSize() : ((WorldlyContainer) container).getSlotsForFace(side).length;
    }

    private int convertSlot(int slot) {
        if(slot < 0) throw new IndexOutOfBoundsException("Cannot access container with negative slot index: " + slot);
        if(side == null) return slot;

        int[] slots = ((WorldlyContainer) container).getSlotsForFace(side);
        if(slot >= slots.length) {
            throw new IndexOutOfBoundsException("Cannot access worldly container on side " + side + ": out of bounds slot index " + slot + " with size " + slots.length);
        }
        return slots[slot];
    }
}
