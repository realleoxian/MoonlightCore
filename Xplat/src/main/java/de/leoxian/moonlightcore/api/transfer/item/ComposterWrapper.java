/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package de.leoxian.moonlightcore.api.transfer.item;

import com.google.common.collect.MapMaker;
import com.mojang.logging.LogUtils;
import de.leoxian.moonlightcore.api.transfer.storage.ExtractionOnlyStorage;
import de.leoxian.moonlightcore.api.transfer.storage.InsertionOnlyStorage;
import de.leoxian.moonlightcore.api.transfer.storage.Storage;
import de.leoxian.moonlightcore.api.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.api.transfer.transaction.TransactionalRandom;
import de.leoxian.moonlightcore.impl.transfer.StoragePreconditions;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
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
import java.util.Objects;

public class ComposterWrapper extends SnapshotJournal<BlockState> {
    private static final Map<WrapperLocation, ComposterWrapper> WRAPPERS = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();
    private static final ItemResource BONE_MEAL = ItemResource.of(Items.BONE_MEAL);

    @Nullable
    public static Storage<ItemResource> get(Level level, BlockPos blockPos, @Nullable Direction direction) {
        Objects.requireNonNull(level, "Level may not be 'null'");
        Objects.requireNonNull(blockPos, "BlockPos may not be 'null'");

        if(direction != null && direction.getAxis().isVertical()) {
            ComposterWrapper wrapper = WRAPPERS.computeIfAbsent(new WrapperLocation(level, blockPos), ComposterWrapper::new);
            return direction == Direction.UP ? wrapper.topStorage : wrapper.bottomStorage;
        }
        return null;
    }

    private final TransactionalRandom random = new TransactionalRandom();
    private final TopStorage topStorage = new TopStorage();
    private final BottomStorage bottomStorage = new BottomStorage();
    private final WrapperLocation location;

    private ComposterWrapper(WrapperLocation location) {
        this.location = location;
    }

    @Override
    public BlockState createSnapshot() {
        return location.getBlockState();
    }

    @Override
    public void revertToSnapshot(BlockState snapshot) {
        location.level.setBlock(location.blockPos, snapshot, 0);
    }

    @Override
    public void onRootCommit(BlockState originalState) {
        BlockState currentState = location.getBlockState();

        if (!currentState.is(Blocks.COMPOSTER)) {
            return;
        }

        if (originalState != currentState) {
            location.level.setBlock(location.blockPos, originalState, 0);
            location.level.setBlockAndUpdate(location.blockPos, currentState);
            location.level.gameEvent(GameEvent.BLOCK_CHANGE, location.blockPos, GameEvent.Context.of(currentState));
        }

        int originalLevel = originalState.getValue(ComposterBlock.LEVEL);
        int currentLevel = currentState.getValue(ComposterBlock.LEVEL);

        if (originalLevel < ComposterBlock.MAX_LEVEL) {
            if (currentLevel == ComposterBlock.MAX_LEVEL) {
                location.level.scheduleTick(location.blockPos, currentState.getBlock(), SharedConstants.TICKS_PER_SECOND);
            }

            location.level.levelEvent(LevelEvent.COMPOSTER_FILL, location.blockPos, currentLevel > originalLevel ? 1 : 0);
        }
    }

    private void setState(BlockState state, int level) {
        BlockState newState = state.setValue(ComposterBlock.LEVEL, level);
        location.level.setBlock(location.blockPos, newState, 0);
    }

    private class TopStorage implements InsertionOnlyStorage<ItemResource> {

        @Override
        public int insert(TransactionContext tx, int index, ItemResource resource, int maxAmount) {
            StoragePreconditions.singleSlotIndexCheck(index);
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);

            if (maxAmount < 1)
                return 0;

            BlockState state = location.getBlockState();
            int currentLevel = state.getValue(ComposterBlock.LEVEL);
            if (currentLevel >= ComposterBlock.MAX_LEVEL)
                return 0;

            float value = getCompostableValue(resource);
            if (value <= 0.0F)
                return 0;

            updateSnapshots(tx);
            if  (currentLevel == ComposterBlock.MIN_LEVEL || random.nextDouble(tx) < value) {
                setState(state, currentLevel + 1);
            }

            return 1;
        }

        @Override
        public int getCapacity(int index, ItemResource resource) {
            StoragePreconditions.singleSlotIndexCheck(index);

            BlockState state = location.getBlockState();
            int currentLevel = state.getValue(ComposterBlock.LEVEL);
            return resource.isBlank() || currentLevel >= ComposterBlock.MAX_LEVEL ? 0 : ComposterBlock.MAX_LEVEL - currentLevel;
        }

        @Override
        public ItemResource getResource(int index) {
            StoragePreconditions.singleSlotIndexCheck(index);
            return ItemResource.blank();
        }

        @Override
        public int getAmount(int index) {
            StoragePreconditions.singleSlotIndexCheck(index);
            return 0;
        }

        @Override
        public boolean isBlank(int index) {
            StoragePreconditions.singleSlotIndexCheck(index);
            return true;
        }

        @Override
        public int size() {
            return 1;
        }

        private float getCompostableValue(ItemResource resource) {
            return resource.isBlank() ? 0.0F : ComposterBlock.COMPOSTABLES.getFloat(resource.get());
        }

    }

    private class BottomStorage implements ExtractionOnlyStorage<ItemResource> {

        @Override
        public int extract(TransactionContext tx, int index, ItemResource resource, int maxAmount) {
            StoragePreconditions.singleSlotIndexCheck(index);
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);

            if (maxAmount < 1 || BONE_MEAL != resource)
                return 0;

            BlockState state = location.getBlockState();
            if (state.getValue(ComposterBlock.LEVEL) == ComposterBlock.READY)
                return 0;

            updateSnapshots(tx);
            setState(state, ComposterBlock.MIN_LEVEL);
            return 1;
        }

        @Override
        public int getCapacity(int index, ItemResource resource) {
            StoragePreconditions.singleSlotIndexCheck(index);
            return resource.isBlank() || BONE_MEAL == resource ? 1 : 0;
        }

        @Override
        public ItemResource getResource(int index) {
            StoragePreconditions.singleSlotIndexCheck(index);
            return location.getBlockState().getValue(ComposterBlock.LEVEL) == ComposterBlock.READY ? BONE_MEAL : ItemResource.blank();
        }

        @Override
        public int getAmount(int index) {
            StoragePreconditions.singleSlotIndexCheck(index);
            return getResource(index) == BONE_MEAL ? 1 : 0;
        }

        @Override
        public boolean isBlank(int index) {
            StoragePreconditions.singleSlotIndexCheck(index);
            return getResource(index).isBlank();
        }

        @Override
        public int size() {
            return 1;
        }

    }

    private record WrapperLocation(Level level, BlockPos blockPos) {

        public void setState(BlockState state) {
            level.setBlockAndUpdate(blockPos, state);
        }

        public BlockState getBlockState() {
            return level.getBlockState(blockPos);
        }

    }
}
