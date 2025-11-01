package de.leoxian.moonlightcore.transfer.item;

import com.google.common.collect.MapMaker;
import de.leoxian.moonlightcore.transfer.*;
import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
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
import org.jetbrains.annotations.Nullable;

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

    private static float getComposterValue(ItemResource resource) {
        return ComposterBlock.COMPOSTABLES.getFloat(resource.get());
    }

    private final TransactionalRandom transactionalRandom = new TransactionalRandom();
    private final TopStorage topStorage = new TopStorage();
    private final BottomStorage bottomStorage = new BottomStorage();
    private final WrapperLocation location;

    ComposterWrapper(WrapperLocation location) {
        this.location = location;
    }

    @Override
    public BlockState createSnapshot() {
        return location.getBlockState();
    }

    @Override
    public void revertToSnapshot(BlockState snapshot) {
        location.level.setBlock(location.pos, snapshot, 0);
    }

    @Override
    public void onRootCommit(BlockState originalState) {
        BlockState currentState = location.getBlockState();

        if(!currentState.is(Blocks.COMPOSTER)) {
            return;
        }

        if(originalState != currentState) {
            location.level.setBlock(location.pos, originalState, 0);
            location.level.setBlockAndUpdate(location.pos, currentState);
            location.level.gameEvent(GameEvent.BLOCK_CHANGE, location.pos, GameEvent.Context.of(currentState));
        }

        int originalLevel = originalState.getValue(ComposterBlock.LEVEL);
        int currentLevel = currentState.getValue(ComposterBlock.LEVEL);

        if(originalLevel < ComposterBlock.MAX_LEVEL) {
            if(currentLevel == ComposterBlock.MAX_LEVEL) {
                location.level.scheduleTick(location.pos, currentState.getBlock(), SharedConstants.TICKS_PER_SECOND);
            }

            location.level.levelEvent(LevelEvent.COMPOSTER_FILL, location.pos, currentLevel > originalLevel ? 1 : 0);
        }
    }

    private void setLevel(BlockState state, int newLevel) {
        BlockState newState = state.setValue(ComposterBlock.LEVEL, newLevel);
        location.level.setBlock(location.pos, newState, 0);
    }

    private record WrapperLocation(Level level, BlockPos pos) {
        private BlockState getBlockState() {
            return level().getBlockState(pos());
        }
    }

    private class TopStorage implements InsertionOnlyStorage<ItemResource>, SingleSlotStorage<ItemResource> {
        @Override
        public int insert(Transaction tx, ItemResource resource, int amount) {
            StorageInternals.checkNonEmptyNonNegative(resource, amount);

            if(amount < 1) return 0;

            BlockState state = location.getBlockState();
            int currentLevel = state.getValue(ComposterBlock.LEVEL);
            if (currentLevel >= ComposterBlock.MAX_LEVEL) return 0;
            float value = getComposterValue(resource);
            if(value <= 0) return 0;

            updateSnapshots(tx);
            if(currentLevel == ComposterBlock.MIN_LEVEL || transactionalRandom.nextDouble(tx) < value) {
                setLevel(state, currentLevel + 1);
            }

            return 1;
        }

        @Override
        public boolean isResourceValid(ItemResource resource) {
            return ComposterBlock.COMPOSTABLES.getFloat(resource.get()) > 0;
        }

        @Override
        public int getCapacity(ItemResource resource) {
            return 0;
        }

        @Override
        public ItemResource resource() {
            return ItemResource.empty();
        }

        @Override
        public int amount() {
            return 0;
        }
    }

    private class BottomStorage implements ExtractionOnlyStorage<ItemResource>, SingleSlotStorage<ItemResource> {
        @Override
        public int extract(Transaction tx, ItemResource resource, int amount) {
            StorageInternals.checkNonEmptyNonNegative(resource, amount);

            if(amount < 1) return 0;
            if(!BONE_MEAL.is(resource.get())) return 0;

            BlockState state = location.getBlockState();
            if(state.getValue(ComposterBlock.LEVEL) != ComposterBlock.READY) return 0;

            updateSnapshots(tx);
            setLevel(state, ComposterBlock.MIN_LEVEL);
            return 1;
        }

        @Override
        public boolean isResourceValid(ItemResource resource) {
            return BONE_MEAL.is(resource.get());
        }

        @Override
        public int getCapacity(ItemResource resource) {
            return 0;
        }

        @Override
        public ItemResource resource() {
            return location.getBlockState().getValue(ComposterBlock.LEVEL) == ComposterBlock.READY ? BONE_MEAL : ItemResource.empty();
        }

        @Override
        public int amount() {
            return 0;
        }
    }
}
