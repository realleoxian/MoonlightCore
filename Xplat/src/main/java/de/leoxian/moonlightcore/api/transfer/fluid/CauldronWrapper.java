/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package de.leoxian.moonlightcore.api.transfer.fluid;

import com.google.common.collect.MapMaker;
import com.google.common.math.IntMath;
import de.leoxian.moonlightcore.api.fluid.CauldronFluidContent;
import de.leoxian.moonlightcore.api.transfer.storage.Storage;
import de.leoxian.moonlightcore.api.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.impl.transfer.StoragePreconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import java.util.Map;

public class CauldronWrapper extends SnapshotJournal<BlockState> implements Storage<FluidResource> {
    private static final Map<WrapperLocation, CauldronWrapper> WRAPPERS = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();

    public static CauldronWrapper get(Level level, BlockPos blockPos) {
        WrapperLocation location = new WrapperLocation(level, blockPos);
        return WRAPPERS.computeIfAbsent(location, CauldronWrapper::new);
    }

    private final WrapperLocation location;

    private CauldronWrapper(WrapperLocation location) {
        this.location = location;
    }

    @Override
    public int insert(TransactionContext tx, int index, FluidResource resource, int maxAmount) {
        StoragePreconditions.singleSlotIndexCheck(index);
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        if (resource.hasTag())
            return 0;

        CauldronFluidContent insertContent = CauldronFluidContent.getForFluid(resource.get());
        if(insertContent == null)
            return 0;

        BlockState state = location.getBlockState();
        CauldronFluidContent currentContent = getContent(state);
        if (currentContent.fluid() != Fluids.EMPTY && !resource.is(currentContent.fluid()))
            return 0;

        int d = IntMath.gcd(insertContent.maxLevel(), insertContent.totalAmount());
        int amountIncrements = insertContent.totalAmount() / d;
        int levelIncrements = insertContent.maxLevel() / d;

        int currentLevel = currentContent.currentLevel(state);
        int insertedIncrements = Math.min(maxAmount / amountIncrements, (insertContent.maxLevel() - currentLevel) / levelIncrements);
        if (insertedIncrements > 0) {
            setState(tx, insertContent, currentLevel + insertedIncrements * levelIncrements);
        }

        return insertedIncrements * amountIncrements;
    }

    @Override
    public int extract(TransactionContext tx, int index, FluidResource resource, int maxAmount) {
        StoragePreconditions.singleSlotIndexCheck(index);
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        BlockState state = location.getBlockState();
        CauldronFluidContent currentContent = getContent(state);
        if (resource.is(currentContent.fluid()) || resource.hasTag())
            return 0;

        int d = IntMath.gcd(currentContent.maxLevel(), currentContent.totalAmount());
        int levelIncrements = currentContent.maxLevel() / d;
        int amountIncrements = currentContent.totalAmount() / d;

        int currentLevel = currentContent.currentLevel(state);
        int extractedIncrements = Math.min(maxAmount / amountIncrements, currentLevel / levelIncrements);
        if (extractedIncrements > 0) {
            setState(tx, currentContent, currentLevel - extractedIncrements * levelIncrements);
        }

        return extractedIncrements * amountIncrements;
    }

    @Override
    public FluidResource getResource(int index) {
        StoragePreconditions.singleSlotIndexCheck(index);

        BlockState state = location.getBlockState();
        return FluidResource.of(getContent(state).fluid());
    }

    @Override
    public int getAmount(int index) {
        StoragePreconditions.singleSlotIndexCheck(index);

        BlockState state = location.getBlockState();
        CauldronFluidContent content = getContent(state);
        return content.totalAmount() * content.currentLevel(state) / content.maxLevel();
    }

    @Override
    public int getCapacity(int index, FluidResource resource) {
        StoragePreconditions.singleSlotIndexCheck(index);

        CauldronFluidContent fluidContent = CauldronFluidContent.getForFluid(resource.get());
        return fluidContent == null ? 0 : fluidContent.totalAmount();
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
        BlockState state = location.getBlockState();

        if (originalState == state || CauldronFluidContent.getForBlock(state.getBlock()) == null)
            return;

        location.level.setBlock(location.blockPos, originalState, 0);
        location.level.setBlockAndUpdate(location.blockPos, state);
    }

    private void setState(TransactionContext tx, CauldronFluidContent content, int fluidLevel) {
        updateSnapshots(tx);

        if (fluidLevel == 0) {
            location.level.setBlock(location.blockPos, Blocks.CAULDRON.defaultBlockState(), 0);
        } else {
            BlockState newState = content.block().defaultBlockState();

            if (content.levelProperty() != null) {
                newState = newState.setValue(content.levelProperty(), fluidLevel);
            }

            location.level.setBlock(location.blockPos, newState, 0);
        }
    }

    private CauldronFluidContent getContent(BlockState state) {
        CauldronFluidContent content = CauldronFluidContent.getForBlock(state.getBlock());
        if (content == null) {
            throw new IllegalStateException("Unexpected error: no cauldron at location " + location.blockPos + " in " + location.level.dimension().location());
        }

        return content;
    }

    private record WrapperLocation(Level level, BlockPos blockPos) {

        public BlockState getBlockState() {
            return level.getBlockState(blockPos);
        }

    }
}
