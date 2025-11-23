package de.leoxian.moonlightcore.transfer.item;

import com.google.common.collect.MapMaker;
import de.leoxian.moonlightcore.transfer.*;
import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Map;

public class ComposterWrapper extends SnapshotJournal<BlockState> {
    private static final Map<WrapperLocation, ComposterWrapper> WRAPPERS = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();
    private static final ItemResource BONE_MEAL = ItemResource.of(Items.BONE_MEAL);

    public static Storage<ItemResource> get(Level level, BlockPos pos, @Nullable Direction direction) {
        if(direction == null || !direction.getAxis().isVertical()) {
            return null;
        }

        WrapperLocation location = new WrapperLocation(level, pos);
        ComposterWrapper wrapper = WRAPPERS.computeIfAbsent(location, ComposterWrapper::new);
        return direction == Direction.UP ? wrapper.topStorage : wrapper.bottomStorage;
    }

    private static float getComposterValue(@Nullable ItemResource resource) {
        if(resource == null || resource.isBlank()) {
            return 0.0F;
        }

        return ComposterBlock.COMPOSTABLES.getFloat(resource.toStack().getItem());
    }

    private final TransactionalRandom transactionalRandom = new TransactionalRandom();
    private final Storage<ItemResource> topStorage = new TopStorage();
    private final Storage<ItemResource> bottomStorage = new BottomStorage();
    private final WrapperLocation wrapperLocation;

    private ComposterWrapper(WrapperLocation wrapperLocation) {
        this.wrapperLocation = wrapperLocation;
    }

    @Override
    public void onRootCommit(BlockState originalState) {
        BlockState currentState = wrapperLocation.getBlockState();
        if(!currentState.is(Blocks.COMPOSTER)) return;

        if(originalState != currentState) {
            wrapperLocation.level.setBlock(wrapperLocation.pos(), originalState, 0);
            wrapperLocation.setBlockState(currentState);
            wrapperLocation.level.gameEvent(GameEvent.BLOCK_CHANGE, wrapperLocation.pos, GameEvent.Context.of(currentState));
        }

        int originalLevel = originalState.getValue(ComposterBlock.LEVEL);
        int currentLevel = currentState.getValue(ComposterBlock.LEVEL);

        if(originalLevel < ComposterBlock.MAX_LEVEL) {
            if(currentLevel == ComposterBlock.MAX_LEVEL) {
                wrapperLocation.level.scheduleTick(wrapperLocation.pos, currentState.getBlock(), SharedConstants.TICKS_PER_SECOND);
            }

            wrapperLocation.level.levelEvent(LevelEvent.COMPOSTER_FILL, wrapperLocation.pos, currentLevel > originalLevel ? 1 : 0);
        }
    }

    @Override
    public BlockState createSnapshot() {
        return wrapperLocation.getBlockState();
    }

    @Override
    public void revertToSnapshot(BlockState snapshot) {
        wrapperLocation.setBlockState(snapshot);
    }

    private void setBlockState(BlockState state, int newLevel) {
        BlockState newState = state.setValue(ComposterBlock.LEVEL, newLevel);
        wrapperLocation.setBlockState(newState);
    }

    private class TopStorage implements InsertionOnlyStorage<ItemResource>, SingleSlotStorage<ItemResource> {
        @Override
        public int insert(TransactionContext context, ItemResource insertedResource, int maxAmount) {
            StoragePreconditions.notBlankNotNegative(insertedResource, maxAmount);
            if(maxAmount < 1) return 0;

            BlockState state = wrapperLocation.getBlockState();
            int currentLevel = state.getValue(ComposterBlock.LEVEL);
            if(currentLevel >= ComposterBlock.MAX_LEVEL) return 0;
            float value = getComposterValue(insertedResource);
            if(value <= 0) {
                return 0;
            }

            updateSnapshots(context);
            if(currentLevel == ComposterBlock.MIN_LEVEL || transactionalRandom.nextDouble(context) < value) {
                setBlockState(state, currentLevel + 1);
            }

            return 1;
        }

        @Override
        public int getCapacity(ItemResource resource) {
            BlockState state = wrapperLocation.getBlockState();
            int currentLevel = state.getValue(ComposterBlock.LEVEL);
            return currentLevel >= ComposterBlock.MAX_LEVEL ? 0 : ComposterBlock.MAX_LEVEL - currentLevel;
        }

        @Override
        public int getAmount() {
            return 0;
        }

        @Override
        public boolean isResourceBlank() {
            return true;
        }

        @Override
        public ItemResource getResource() {
            return ItemResource.blank();
        }
    }

    private class BottomStorage implements ExtractionOnlyStorage<ItemResource>, SingleSlotStorage<ItemResource> {
        @Override
        public int extract(TransactionContext context, ItemResource extractedResource, int maxAmount) {
            StoragePreconditions.notBlankNotNegative(extractedResource, maxAmount);
            if(maxAmount < 1 || !BONE_MEAL.equals(extractedResource)) return 0;

            BlockState state = wrapperLocation.getBlockState();
            if(state.getValue(ComposterBlock.LEVEL) != ComposterBlock.MAX_LEVEL) {
                return 0;
            }

            updateSnapshots(context);
            setBlockState(state, ComposterBlock.MIN_LEVEL);
            return 1;
        }

        @Override
        public int getCapacity(ItemResource resource) {
            return BONE_MEAL.equals(resource) ? 1 : 0;
        }

        @Override
        public int getAmount() {
            BlockState state = wrapperLocation.getBlockState();
            return state.getValue(ComposterBlock.LEVEL) == ComposterBlock.MAX_LEVEL ? 1 : 0;
        }

        @Override
        public boolean isResourceBlank() {
            return getAmount() == 0;
        }

        @Override
        public ItemResource getResource() {
            return getAmount() > 0 ? BONE_MEAL : ItemResource.blank();
        }
    }

    private record WrapperLocation(Level level, BlockPos pos) {
        private BlockState getBlockState() {
            return level.getBlockState(pos);
        }

        private void setBlockState(BlockState state) {
            level.setBlockAndUpdate(pos, state);
        }
    }
}
