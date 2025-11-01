package de.leoxian.moonlightcore.transfer.item;

import com.google.common.collect.MapMaker;
import de.leoxian.moonlightcore.transfer.CombinedStorage;
import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.SpecialLogicInventory;
import de.leoxian.moonlightcore.transfer.Storage;
import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VanillaContainerWrapper extends CombinedStorage<Item, ItemResource, SingleSlotStorage<Item, ItemResource>> {
    private static final Map<Container, VanillaContainerWrapper> WRAPPERS = new MapMaker().weakKeys().weakValues().makeMap();

    public static Storage<Item, ItemResource> of(Container container) {
        return internalOf(container);
    }

    private static VanillaContainerWrapper internalOf(Container container) {
        VanillaContainerWrapper wrapper = WRAPPERS.computeIfAbsent(container, cont -> {
            if(cont instanceof Inventory inventory) {
                return new InventoryStorage(inventory);
            } else {
                return new VanillaContainerWrapper(cont);
            }
        });

        wrapper.resize();
        return wrapper;
    }

    private final MarkDirtyJournal markDirtyJournal = new MarkDirtyJournal();
    protected final List<SlotWrapper> slotWrappers = new ArrayList<>();
    protected final Container container;

    protected VanillaContainerWrapper(Container container) {
        this(Collections.emptyList(), container);
    }

    protected VanillaContainerWrapper(List<SingleSlotStorage<Item, ItemResource>> slots, Container container) {
        super(slots);
        this.container = container;
    }

    public VanillaContainerWrapper getSidedWrapper(@Nullable Direction direction) {
        if(container instanceof WorldlyContainer && direction != null) {
            return new WordlyInventoryStorage(this, direction);
        }

        return this;
    }

    public List<SingleSlotStorage<Item, ItemResource>> getSlots() {
        return Arrays.asList(this.storages);
    }

    @SuppressWarnings("unchecked")
    protected void resize() {
        int containerSize = this.container.getContainerSize();

        if(containerSize != this.storages.length) {
            while(this.slotWrappers.size() < containerSize) {
                this.slotWrappers.add(new SlotWrapper(this, this.slotWrappers.size()));
            }

            this.storages = (SingleSlotStorage<Item, ItemResource>[]) Collections.unmodifiableList(this.slotWrappers.subList(0, containerSize)).toArray(Storage[]::new);
        }
    }

    protected static class SlotWrapper extends SingleStackStorage {
        private final VanillaContainerWrapper wrapper;
        @Nullable
        private final SpecialLogicInventory specialInv;
        final int slot;

        SlotWrapper(VanillaContainerWrapper wrapper, int slot) {
            this.specialInv = wrapper.container instanceof SpecialLogicInventory ? (SpecialLogicInventory) wrapper.container : null;
            this.wrapper = wrapper;
            this.slot = slot;
        }

        @Override
        public ItemStack getStack() {
            return wrapper.container.getItem(this.slot);
        }

        @Override
        public void setStack(ItemStack stack) {
            if(specialInv == null) {
                this.wrapper.container.setItem(slot, stack);
            } else {
                specialInv.mlcore_setSuppress(true);

                try {
                    this.wrapper.container.setItem(slot, stack);
                } finally {
                    specialInv.mlcore_setSuppress(false);
                }
            }
        }

        @Override
        public int insert(Transaction tx, ItemResource resource, int amount) {
            if(!canInsert(this.slot, resource.getCachedStack())) {
                return 0;
            }

            int inserted = super.insert(tx, resource, amount);
            if(this.specialInv != null && inserted > 0) {
                this.specialInv.mlcore_onTransfer(tx, this.slot);
            }

            return inserted;
        }

        @Override
        public int extract(Transaction tx, ItemResource resource, int amount) {
            int extracted = super.extract(tx, resource, amount);
            if(this.specialInv != null && extracted > 0) {
                this.specialInv.mlcore_onTransfer(tx, this.slot);
            }

            return extracted;
        }

        @Override
        public int getCapacity(ItemResource resource) {
            if(this.wrapper.container instanceof AbstractFurnaceBlockEntity && slot == 1 && resource.is(Items.BUCKET)) {
                return 1;
            }

            if(this.wrapper.container instanceof BrewingStandBlockEntity && slot < 3) {
                return 1;
            }

            return Math.min(this.wrapper.container.getMaxStackSize(), resource.get().getMaxStackSize());
        }

        @Override
        public void updateSnapshots(TransactionContext ctx) {
            this.wrapper.markDirtyJournal.updateSnapshots(ctx);
            super.updateSnapshots(ctx);

            if(this.wrapper.container instanceof ChestBlockEntity chest && chest.getBlockState().getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
                BlockPos otherChestPos = chest.getBlockPos().relative(ChestBlock.getConnectedDirection(chest.getBlockState()));

                if(chest.getLevel().getBlockEntity(otherChestPos) instanceof ChestBlockEntity otherChest) {
                    VanillaContainerWrapper.internalOf(otherChest).markDirtyJournal.updateSnapshots(ctx);
                }
            }
        }

        @Override
        public void onRootCommit(ItemStack originalState) {
            ItemStack currentStack = getStack();

            if(!originalState.isEmpty() && originalState.getItem() == currentStack.getItem()) {
                originalState.setCount(currentStack.getCount());
                originalState.setTag(currentStack.hasTag() ? currentStack.getTag().copy() : null);
                setStack(originalState);
            } else {
                originalState.setCount(0);
            }
        }

        private boolean canInsert(int slot, ItemStack stack) {
            if(this.wrapper.container instanceof ShulkerBoxBlockEntity shulker) {
                return shulker.canPlaceItemThroughFace(slot, stack, null);
            } else {
                return this.wrapper.container.canPlaceItem(slot, stack);
            }
        }
    }

    class MarkDirtyJournal extends SnapshotJournal<Boolean> {
        @Override
        public Boolean createSnapshot() {
            return Boolean.TRUE;
        }

        @Override
        public void revertToSnapshot(Boolean snapshot) {

        }

        @Override
        public void onRootCommit(Boolean originalState) {
            container.setChanged();
        }
    }
}
