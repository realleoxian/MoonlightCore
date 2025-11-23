package de.leoxian.moonlightcore.transfer.item;

import com.google.common.collect.MapMaker;
import de.leoxian.moonlightcore.transfer.*;
import de.leoxian.moonlightcore.transfer.transaction.RootCommitJournal;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class VanillaContainerWrapper extends CombinedStorage<ItemResource, SingleSlotStorage<ItemResource>> {
    private static final Map<Container, VanillaContainerWrapper> WRAPPERS = new MapMaker().weakKeys().weakValues().makeMap();

    public static VanillaContainerWrapper of(Container container, @Nullable Direction direction) {
        VanillaContainerWrapper wrapper = WRAPPERS.computeIfAbsent(container, inv -> {
            if(inv instanceof Inventory inventory) {
                return new InventoryWrapper(inventory);
            } else {
                return new VanillaContainerWrapper(container);
            }
        });

        wrapper.resize();
        return wrapper.getSidedWrapper(direction);
    }

    final List<SingleSlotStorage<ItemResource>> slotWrappers = new ArrayList<>();

    private final RootCommitJournal setChangedJournal;
    final Container container;
    int size;

    VanillaContainerWrapper(Container container) {
        this(Collections.emptyList(), container);
    }

    VanillaContainerWrapper(List<SingleSlotStorage<ItemResource>> parts, Container container) {
        super(parts);
        this.container = container;
        this.setChangedJournal = new RootCommitJournal(container::setChanged);
    }

    @Override
    public int insert(TransactionContext context, ItemResource insertedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(insertedResource, maxAmount);

        int remaining = maxAmount;
        for(SingleSlotStorage<ItemResource> wrapper : this.slotWrappers) {
            remaining -= wrapper.insert(context, insertedResource, maxAmount - remaining);

            if(remaining == 0) {
                break;
            }
        }

        return maxAmount - remaining;
    }

    @Override
    public int extract(TransactionContext context, ItemResource extractedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(extractedResource, maxAmount);

        int remaining = maxAmount;
        for(SingleSlotStorage<ItemResource> wrapper : this.slotWrappers) {
            remaining -= wrapper.extract(context, extractedResource, maxAmount);

            if(remaining == 0) {
                break;
            }
        }

        return maxAmount - remaining;
    }

    @Override
    public int insert(TransactionContext context, int index, ItemResource insertedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(insertedResource, maxAmount);
        return getSlotWrapper(index).insert(context, insertedResource, maxAmount);
    }

    @Override
    public int extract(TransactionContext context, int index, ItemResource extractedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(extractedResource, maxAmount);
        return getSlotWrapper(index).extract(context, extractedResource, maxAmount);
    }

    @Override
    public int size() {
        return this.size;
    }

    @UnmodifiableView
    public List<SingleSlotStorage<ItemResource>> getSlots() {
        return this.slotWrappers;
    }

    SlotWrapper getSlotWrapper(int index) {
        Objects.checkIndex(index, this.size());
        return (SlotWrapper) this.slotWrappers.get(index);
    }

    void resize() {
        size = container.getContainerSize();
        while(this.slotWrappers.size() < size) {
            slotWrappers.add(new SlotWrapper(slotWrappers.size()));
        }
    }

    private VanillaContainerWrapper getSidedWrapper(@Nullable Direction direction) {
        if(container instanceof WorldlyContainer && direction != null) {
            return new WorldlyContainerWrapper(this, direction);
        }

        return this;
    }

    class SlotWrapper extends SingleStackStorage {
        final int index;
        final @Nullable SpecialLogicInventory specialInv;

        SlotWrapper(int index) {
            this.index = index;
            this.specialInv = container instanceof SpecialLogicInventory specialInv ? specialInv : null;
        }

        @Override
        public int insert(TransactionContext context, ItemResource insertedResource, int maxAmount) {
            if(!canInsert(this.index, insertedResource.getCachedStack())) {
                return 0;
            }

            int inserted = super.insert(context, insertedResource, maxAmount);
            if(specialInv != null && inserted > 0) {
                specialInv.mlcore_onTransfer(context, this.index);
            }

            return inserted;
        }

        @Override
        public int extract(TransactionContext context, ItemResource extractedResource, int maxAmount) {
            int extracted = super.extract(context, extractedResource, maxAmount);
            if(specialInv != null && extracted > 0) {
               specialInv.mlcore_onTransfer(context, this.index);
            }

            return extracted;
        }

        @Override
        public void setStack(ItemStack stack) {
            if(specialInv == null) {
                container.setItem(this.index, stack);
            } else {
                specialInv.mlcore_setSuppress(true);

                try {
                    container.setItem(this.index, stack);
                } finally {
                    specialInv.mlcore_setSuppress(false);
                }
            }
        }

        @Override
        public ItemStack getStack() {
            return container.getItem(this.index);
        }

        @Override
        public int getCapacity(ItemResource resource) {
            if(container instanceof AbstractFurnaceBlockEntity && index == 1 && resource.isOf(Items.BUCKET)) {
                return 1;
            }

            if(container instanceof BrewingStandBlockEntity && index < 3) {
                return 1;
            }

            return Math.min(container.getMaxStackSize(), resource.getResource().getMaxStackSize());
        }

        @Override
        public void updateSnapshots(TransactionContext ctx) {
            setChangedJournal.updateSnapshots(ctx);
            super.updateSnapshots(ctx);

            if(container instanceof ChestBlockEntity chest && chest.getBlockState().getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
                BlockPos otherChestPos = chest.getBlockPos().relative(ChestBlock.getConnectedDirection(chest.getBlockState()));

                if(chest.getLevel().getBlockEntity(otherChestPos) instanceof ChestBlockEntity otherChest) {
                    VanillaContainerWrapper.of(otherChest, null).setChangedJournal.updateSnapshots(ctx);
                }
            }
        }

        @Override
        public void onRootCommit(ItemStack originalState) {
            ItemStack currentStack = getStack();

            if(!originalState.isEmpty() && originalState.getItem() == currentStack.getItem()) {
                originalState.setCount(currentStack.getCount());
                originalState.setTag(currentStack.hasTag() ? currentStack.getTag() : null);

                setStack(originalState);
            } else {
                originalState.setCount(0);
            }
        }

        @Override
        public String toString() {
            return "VanillaContainerWrapper[container=" + container + ", slot=" + this.index + "]";
        }

        private boolean canInsert(int index, ItemStack stack) {
            if(container instanceof ShulkerBoxBlockEntity shulker) {
                return shulker.canPlaceItemThroughFace(index, stack, null);
            } else {
                return container.canPlaceItem(index, stack);
            }
        }
    }
}
