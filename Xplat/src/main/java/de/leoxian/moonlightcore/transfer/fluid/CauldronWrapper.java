package de.leoxian.moonlightcore.transfer.fluid;

import com.google.common.collect.MapMaker;
import com.google.common.math.IntMath;
import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.StorageInternals;
import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Map;

public class CauldronWrapper extends SnapshotJournal<BlockState> implements SingleSlotStorage<Fluid, FluidResource> {
    private static final Map<WrapperLocation, CauldronWrapper> WRAPPERS = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();

    public static CauldronWrapper of(Level level, BlockPos pos) {
        WrapperLocation location = new WrapperLocation(level, pos.immutable());
        return WRAPPERS.computeIfAbsent(location, CauldronWrapper::new);
    }

    private final WrapperLocation location;

    CauldronWrapper(WrapperLocation location) {
        this.location = location;
    }

    @Override
    public int insert(Transaction tx, FluidResource resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        if(resource.hasNBT()) {
            return 0;
        }

        CauldronFluidContent insertContent = CauldronFluidContent.getForFluid(resource.get());
        if(insertContent == null) {
            return 0;
        }

        BlockState state = location.getBlockState();
        CauldronFluidContent currentContent = getContent(state);
        if(currentContent.fluid != Fluids.EMPTY && !resource.is(currentContent.fluid)) {
            return 0;
        }

        int d = IntMath.gcd(insertContent.maxLevel, insertContent.amountPerLevel);
        int amountIncrements =insertContent.amountPerLevel / d;
        int levelIncrements = insertContent.maxLevel / d;

        int currentLevel = currentContent.currentLevel(state);
        int insertedIncrements = Math.min(amount / amountIncrements, (insertContent.maxLevel - currentLevel) / levelIncrements);

        if(insertedIncrements > 0) {
            setLevel(tx, insertContent, currentLevel + insertedIncrements * levelIncrements);
        }

        return insertedIncrements * amountIncrements;
    }

    @Override
    public int extract(Transaction tx, FluidResource resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        BlockState state = location.getBlockState();
        CauldronFluidContent currentContent = getContent(state);

        if(!resource.is(currentContent.fluid) || resource.hasNBT()) {
            return 0;
        }

        int d = IntMath.gcd(currentContent.maxLevel, currentContent.amountPerLevel);
        int levelIncrements = currentContent.maxLevel / d;
        int amountIncrements = currentContent.amountPerLevel / d;

        int currentLevel = currentContent.currentLevel(state);
        int extractedIncrements = Math.min(amount / amountIncrements, currentLevel / levelIncrements);

        if(extractedIncrements > 0) {
            setLevel(tx, currentContent, currentLevel - extractedIncrements * levelIncrements);
        }

        return extractedIncrements * amountIncrements;
    }

    @Override
    public void revertToSnapshot(BlockState snapshot) {
        location.level.setBlock(location.pos, snapshot, 0);
    }

    @Override
    public void onRootCommit(BlockState originalState) {
        BlockState state = location.getBlockState();

        if(originalState == state || CauldronFluidContent.getForBlock(state.getBlock()) == null) {
            return;
        }

        location.level.setBlock(location.pos, originalState, 0);
        location.level.setBlockAndUpdate(location.pos, state);
    }

    @Override
    public BlockState createSnapshot() {
        return location.getBlockState();
    }

    private void setLevel(Transaction tx, CauldronFluidContent newContent, int fluidLevel) {
        updateSnapshots(tx);

        if(fluidLevel == 0) {
            this.location.level.setBlock(location.pos, Blocks.CAULDRON.defaultBlockState(), 0);
        } else {
            BlockState newState = newContent.block.defaultBlockState();

            if(newContent.levelProperty != null) {
                newState = newState.setValue(newContent.levelProperty, fluidLevel);
            }

            this.location.level.setBlock(location.pos, newState, 0);
        }
    }

    private CauldronFluidContent getContent(BlockState state) {
        CauldronFluidContent content = CauldronFluidContent.getForBlock(state.getBlock());

        if(content == null) {
            throw new IllegalStateException("Unexpected error: No cauldron at location " + location);
        }

        return content;
    }

    @Override
    public boolean isResourceValid(FluidResource resource) {
        StorageInternals.checkNonEmpty(resource);
        return !resource().hasNBT() && CauldronFluidContent.getForFluid(resource.get()) != null;
    }

    @Override
    public int getCapacity(FluidResource resource) {
        CauldronFluidContent fluidContent = CauldronFluidContent.getForFluid(resource.get());
        return fluidContent == null ? 0 : fluidContent.maxLevel;
    }

    @Override
    public FluidResource resource() {
        BlockState state = location.getBlockState();
        return FluidResource.of(getContent(state).fluid);
    }

    @Override
    public int amount() {
        BlockState state = location.getBlockState();
        CauldronFluidContent content = getContent(state);

        return content.maxLevel * content.currentLevel(state) / content.maxLevel;
    }

    private record WrapperLocation(Level level, BlockPos pos) {
        public BlockState getBlockState() {
            return level.getBlockState(pos);
        }
    }
}
