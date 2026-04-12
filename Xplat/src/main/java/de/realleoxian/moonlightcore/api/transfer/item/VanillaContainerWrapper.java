package de.realleoxian.moonlightcore.api.transfer.item;

import com.google.common.collect.MapMaker;
import de.realleoxian.moonlightcore.api.transfer.storage.Storage;
import de.realleoxian.moonlightcore.api.transfer.transaction.SnapshotJournal;
import de.realleoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.realleoxian.moonlightcore.impl.transfer.item.SpecialLogicInventory;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VanillaContainerWrapper implements Storage<ItemResource> {
    private static final Map<Container, VanillaContainerWrapper> WRAPPERS = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();

    public static VanillaContainerWrapper of(Container container) {
        return of(container, null);
    }

    public static VanillaContainerWrapper of(Container container, @Nullable Direction direction) {
        VanillaContainerWrapper wrapper = WRAPPERS.computeIfAbsent(container, k -> {
            if(k instanceof Inventory inventory) {
                return new PlayerInventoryWrapper(inventory);
            }

            return new VanillaContainerWrapper(k);
        });
        wrapper.ensureCapacity();
        return wrapper.getSidedWrapper(direction);
    }

    private final List<SlotWrapper> slots = new ArrayList<>();
    private final SetChangedJournal setChangedJournal = new SetChangedJournal();

    protected final @Nullable SpecialLogicInventory specialInv;
    protected final Container container;
    protected int size;

    protected VanillaContainerWrapper(Container container) {
        this.specialInv = container instanceof SpecialLogicInventory ?
                (SpecialLogicInventory) container :
                null;

        this.container = container;
    }

    @Override
    public int insert(TransactionContext tx, int index, ItemResource resource, int maxAmount) {
        return getSlotWrapper(index).insert(tx, 0, resource, maxAmount);
    }

    @Override
    public int extract(TransactionContext tx, int index, ItemResource resource, int maxAmount) {
        return getSlotWrapper(index).extract(tx, 0, resource, maxAmount);
    }

    @Override
    public ItemResource getResource(int index) {
        return getSlotWrapper(index).getResource(0);
    }

    @Override
    public int getAmount(int index) {
        return getSlotWrapper(index).getAmount(0);
    }

    @Override
    public int getCapacity(int index, ItemResource resource) {
        return getSlotWrapper(index).getCapacity(0, resource);
    }

    @Override
    public boolean isBlank(int index) {
        return getSlotWrapper(index).isBlank(0);
    }

    @Override
    public int size() {
        return slots.size();
    }

    protected SlotWrapper getSlotWrapper(int index) {
        Objects.checkIndex(index, size());
        return slots.get(index);
    }

    protected void ensureCapacity() {
        int containerSize = container.getContainerSize();

        if(containerSize != slots.size()) {
            while(slots.size() < containerSize) {
                slots.add(new SlotWrapper(slots.size()));
            }
        }
    }

    private VanillaContainerWrapper getSidedWrapper(@Nullable Direction direction) {
        if (container instanceof WorldlyContainer worldlyContainer && direction != null) {
            return new WorldlyContainerWrapper(worldlyContainer, this, direction);
        }

        return this;
    }

    class SlotWrapper extends SingleStackStorage {
        private final int index;

        protected SlotWrapper(int index) {
            this.index = index;
        }

        @Override
        public int insert(TransactionContext tx, int index, ItemResource resource, int maxAmount) {
            int inserted = super.insert(tx, index, resource, maxAmount);
            if (inserted > 0) {
                specialInv.moonlightcore$onTransfer(tx, this.index);
            }

            return inserted;
        }

        @Override
        public int extract(TransactionContext tx, int index, ItemResource resource, int maxAmount) {
            int extracted = super.extract(tx, index, resource, maxAmount);
            if (extracted > 0) {
                specialInv.moonlightcore$onTransfer(tx, this.index);
            }

            return extracted;
        }

        @Override
        public boolean canInsert(int index, ItemResource resource) {
            return container.canPlaceItem(index, resource.toStack());
        }

        @Override
        public void setStack(ItemStack stack) {
            if(specialInv == null) {
                container.setItem(index, stack);
            } else {
                specialInv.moonlightcore$setSupress(true);

                try {
                    container.setItem(index, stack);
                } finally {
                    specialInv.moonlightcore$setSupress(false);
                }
            }
        }

        @Override
        public ItemStack getStack() {
            return container.getItem(index);
        }

        @Override
        public void updateSnapshots(TransactionContext transaction) {
            setChangedJournal.updateSnapshots(transaction);
            super.updateSnapshots(transaction);

            if (container instanceof ChestBlockEntity chest && chest.getBlockState().getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
                BlockPos otherChestPost = chest.getBlockPos().relative(ChestBlock.getConnectedDirection(chest.getBlockState()));

                if (chest.getLevel().getBlockEntity(otherChestPost) instanceof ChestBlockEntity otherChest) {
                    VanillaContainerWrapper.of(otherChest).setChangedJournal.updateSnapshots(transaction);
                }
            }
        }
    }

    private class SetChangedJournal extends SnapshotJournal<@Nullable Void> {

        @Override
        public @Nullable Void createSnapshot() {
            return null;
        }

        @Override
        public void revertToSnapshot(@Nullable Void snapshot) {

        }

        @Override
        public void onRootCommit(@Nullable Void originalState) {
            container.setChanged();
        }

    }
}
