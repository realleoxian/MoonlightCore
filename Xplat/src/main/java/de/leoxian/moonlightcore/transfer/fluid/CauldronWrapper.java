package de.leoxian.moonlightcore.transfer.fluid;

import com.google.common.collect.MapMaker;
import com.google.common.primitives.Ints;
import de.leoxian.moonlightcore.fluid.CauldronFluidContent;
import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.StoragePreconditions;
import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.util.nullness.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class CauldronWrapper extends SnapshotJournal<BlockState> implements SingleSlotStorage<FluidResource> {
    private static final Map<WrapperLocation, CauldronWrapper> WRAPPERS = new MapMaker().concurrencyLevel(1).weakValues().makeMap();

    public static CauldronWrapper get(Level level, BlockPos pos) {
        WrapperLocation location = new WrapperLocation(level, pos.immutable());
        return WRAPPERS.computeIfAbsent(location, CauldronWrapper::new);
    }

    private final WrapperLocation wrapperLocation;

    CauldronWrapper(WrapperLocation wrapperLocation) {
        this.wrapperLocation = wrapperLocation;
    }

    @Override
    public int insert(TransactionContext context, FluidResource insertedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(insertedResource, maxAmount);
        CauldronFluidContent insertContent = CauldronFluidContent.getForFluid(insertedResource.getResource());

        if(insertContent != null) {
            int maxLevelsInserted = Ints.saturatedCast(maxAmount / insertContent.amountPerLevel);

            if(getAmount() == 0) {
                int levelsInserted = Math.min(maxLevelsInserted, insertContent.maxLevel);

                if(levelsInserted > 0) {
                    updateBlockState(context, insertContent, levelsInserted);
                }

                return levelsInserted * insertContent.amountPerLevel;
            }

            CauldronFluidContent currentContent = getCurrentContent();
            if(insertedResource.isOf(currentContent.fluid)) {
                int currentLevel = currentContent.currentLevel(createSnapshot());
                int levelsInserted = Math.min(maxLevelsInserted, currentContent.maxLevel - currentLevel);

                if(levelsInserted > 0) {
                    updateBlockState(context, currentContent, currentLevel + levelsInserted);
                }

                return levelsInserted * currentContent.amountPerLevel;
            }
        }

        return 0;
    }

    @Override
    public int extract(TransactionContext context, FluidResource extractedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(extractedResource, maxAmount);
        CauldronFluidContent currentContent = getCurrentContent();

        if(extractedResource.isOf(currentContent.fluid)) {
            int maxLevelsExtracted = Ints.saturatedCast(maxAmount / currentContent.amountPerLevel);
            int currentLevel = currentContent.currentLevel(createSnapshot());
            int levelsExtracted = Math.min(maxLevelsExtracted, currentLevel);

            if(levelsExtracted > 0) {
                if(levelsExtracted == currentLevel) {
                    updateSnapshots(context);
                    wrapperLocation.setState(Blocks.CAULDRON.defaultBlockState());
                } else {
                    updateBlockState(context, currentContent, currentLevel - levelsExtracted);
                }
            }

            return levelsExtracted * currentContent.amountPerLevel;
        }

        return 0;
    }

    @Override
    public BlockState createSnapshot() {
        return wrapperLocation.getBlockState();
    }

    @Override
    public void revertToSnapshot(BlockState snapshot) {
        wrapperLocation.setState(snapshot);
    }

    @Override
    public void onRootCommit(BlockState originalState) {
        BlockState currentState = createSnapshot();

        if(currentState == originalState || CauldronFluidContent.getForBlock(currentState.getBlock()) == null) {
            return;
        }

        wrapperLocation.setState(originalState);
        wrapperLocation.level.setBlockAndUpdate(wrapperLocation.pos, currentState);
    }

    @Override
    public int getCapacity(FluidResource resource) {
        CauldronFluidContent content = getCurrentContent();
        return content.maxLevel * content.amountPerLevel;
    }

    @Override
    public int getAmount() {
        CauldronFluidContent content = getCurrentContent();
        return content.currentLevel(createSnapshot()) * content.amountPerLevel;
    }

    @Override
    public boolean isResourceBlank() {
        return getResource().isBlank();
    }

    @Override
    public FluidResource getResource() {
        CauldronFluidContent content = getCurrentContent();
        return FluidResource.of(content.fluid);
    }

    private CauldronFluidContent getCurrentContent() {
        CauldronFluidContent content = CauldronFluidContent.getForBlock(createSnapshot().getBlock());
        if(content == null) {
            throw new IllegalStateException("Unexpected error: No cauldron at " + this.wrapperLocation);
        }

        return content;
    }

    private void updateBlockState(TransactionContext ctx, CauldronFluidContent newContent, int level) {
        updateSnapshots(ctx);

        BlockState newState = newContent.block.defaultBlockState();
        if(newContent.levelProperty != null) {
            newState = newState.setValue(newContent.levelProperty, level);
        }

        wrapperLocation.level.setBlock(wrapperLocation.pos, newState, 0);

    }

    private record WrapperLocation(Level level, BlockPos pos) {
        void setState(BlockState newState) {
            level.setBlock(pos, newState, 0);
        }

        BlockState getBlockState() {
            return level.getBlockState(pos);
        }

        @Override
        public @Nonnull String toString() {
            return "WrapperLocation[x=%d,y=%d,z=%d/Cauldron]".formatted(pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
