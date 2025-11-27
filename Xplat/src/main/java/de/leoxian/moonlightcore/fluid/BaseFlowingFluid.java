package de.leoxian.moonlightcore.fluid;

import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class BaseFlowingFluid extends FlowingFluid {
    private final Supplier<? extends Fluid> flowing;
    private final Supplier<? extends Fluid> still;
    private final @Nullable Supplier<? extends Item> bucket;
    private final @Nullable Supplier<? extends LiquidBlock> block;
    private final int slopeFindDistance;
    private final int levelDecreasePerBlock;
    private final float explosionResistance;
    private final int tickRate;

    private final @Nullable Supplier<SoundEvent> pickupSound;

    protected BaseFlowingFluid(Properties properties) {
        this.still = properties.still;
        this.flowing = properties.flowing;
        this.bucket = properties.bucket;
        this.block = properties.block;
        this.slopeFindDistance = properties.slopeFindDistance;
        this.levelDecreasePerBlock = properties.levelDecreasePerBlock;
        this.explosionResistance = properties.explosionResistance;
        this.tickRate = properties.tickRate;
        this.pickupSound = properties.pickupSound;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        BlockEntity be = blockState.hasBlockEntity() ? levelAccessor.getBlockEntity(blockPos) : null;
        Block.dropResources(blockState, levelAccessor, blockPos, be);
    }

    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN || !isSame(fluid);
    }

    @Override
    protected BlockState createLegacyBlock(FluidState fluidState) {
        if(block != null) {
            LiquidBlock lb = block.get();
            return lb.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState));
        }

        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == still.get() || fluid == flowing.get();
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }

    @Override
    protected int getSlopeFindDistance(LevelReader levelReader) {
        return slopeFindDistance;
    }

    @Override
    protected int getDropOff(LevelReader levelReader) {
        return levelDecreasePerBlock;
    }

    @Override
    public int getTickDelay(LevelReader levelReader) {
        return tickRate;
    }

    @Override
    public float getExplosionResistance() {
        return explosionResistance;
    }

    @Override
    public Item getBucket() {
        return bucket != null ? bucket.get() : Items.AIR;
    }

    @Override
    public Fluid getFlowing() {
        return flowing.get();
    }

    @Override
    public Fluid getSource() {
        return still.get();
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        if(pickupSound == null) return Optional.empty();
        return Optional.ofNullable(pickupSound.get());
    }

    public static class Flowing extends BaseFlowingFluid {
        public Flowing(Properties properties) {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(LiquidBlock.LEVEL, 7));
        }

        @Override
        public int getAmount(FluidState fluidState) {
            return fluidState.getValue(LiquidBlock.LEVEL);
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return false;
        }
    }

    public static class Source extends BaseFlowingFluid {
        public Source(Properties properties) {
            super(properties);
        }

        @Override
        public int getAmount(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return true;
        }
    }

    public static class Properties {
        private final Supplier<? extends Fluid> still;
        private final Supplier<? extends Fluid> flowing;
        private @Nullable Supplier<? extends Item> bucket;
        private @Nullable Supplier<? extends LiquidBlock> block;
        private @Nullable Supplier<SoundEvent> pickupSound;
        private int slopeFindDistance = 4;
        private int levelDecreasePerBlock = 1;
        private float explosionResistance = 1.0F;
        private int tickRate = 5;

        public Properties(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing) {
            this.still = still;
            this.flowing = flowing;
        }

        public Properties pickupSound(@Nullable Supplier<SoundEvent> pickupSound) {
            this.pickupSound = pickupSound;
            return this;
        }

        public Properties bucket(@Nullable Supplier<? extends Item> bucket) {
            this.bucket = bucket;
            return this;
        }

        public Properties block(@Nullable Supplier<? extends LiquidBlock> block) {
            this.block = block;
            return this;
        }

        public Properties slopeFindDistance(int slopeFindDistance) {
            this.slopeFindDistance = slopeFindDistance;
            return this;
        }

        public Properties levelDecreasePerBlock(int levelDecreasePerBlock) {
            this.levelDecreasePerBlock = levelDecreasePerBlock;
            return this;
        }

        public Properties explosionResistance(float explosionResistance) {
            this.explosionResistance = explosionResistance;
            return this;
        }

        public Properties tickRate(int tickRate) {
            this.tickRate = tickRate;
            return this;
        }
    }
}
