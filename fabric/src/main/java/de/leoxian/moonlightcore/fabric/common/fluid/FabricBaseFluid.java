package de.leoxian.moonlightcore.fabric.common.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.function.Supplier;

public abstract class FabricBaseFluid extends FlowingFluid {
	private final Supplier<? extends Fluid> flowingGetter;
	private final Supplier<? extends Fluid> sourceGetter;
	private final Supplier<? extends Item> bucketGetter;
	private final Supplier<? extends Block> blockGetter;
	private final int slopeFindDistance;
	private final int levelDecreasePerBlock;
	private final float explosionResistance;
	private final int tickRate;

	FabricBaseFluid(Supplier<? extends Fluid> flowingGetter, Supplier<? extends Fluid> sourceGetter,
					Supplier<? extends Item> bucketGetter, Supplier<? extends Block> blockGetter,
					int slopeFindDistance, int levelDecreasePerBlock, float explosionResistance, int tickRate) {
		this.flowingGetter = flowingGetter;
		this.sourceGetter = sourceGetter;
		this.bucketGetter = bucketGetter;
		this.blockGetter = blockGetter;
		this.slopeFindDistance = slopeFindDistance;
		this.levelDecreasePerBlock = levelDecreasePerBlock;
		this.explosionResistance = explosionResistance;
		this.tickRate = tickRate;
	}

	@Override
	public Fluid getFlowing() {
		return this.flowingGetter.get();
	}

	@Override
	public Fluid getSource() {
		return this.sourceGetter.get();
	}

	@Override
	public boolean isSame(Fluid fluid) {
		return fluid == getSource() || fluid == getFlowing();
	}

	@Override
	protected boolean canConvertToSource(ServerLevel level) {
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
		Block.dropResources(state, level, pos, blockEntity);
	}

	@Override
	protected int getSlopeFindDistance(LevelReader level) {
		return this.slopeFindDistance;
	}

	@Override
	protected int getDropOff(LevelReader level) {
		return this.levelDecreasePerBlock;
	}

	@Override
	public Item getBucket() {
		return this.bucketGetter.get();
	}

	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid other, Direction direction) {
		return direction == Direction.DOWN && !isSame(other);
	}

	@Override
	public int getTickDelay(LevelReader level) {
		return this.tickRate;
	}

	@Override
	protected float getExplosionResistance() {
		return this.explosionResistance;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState fluidState) {
		return this.blockGetter.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState));
	}

	public static final class Source extends FabricBaseFluid {
		public Source(Supplier<? extends Fluid> flowingGetter, Supplier<? extends Fluid> sourceGetter,
					Supplier<? extends Item> bucketGetter, Supplier<? extends Block> blockGetter,
					int slopeFindDistance, int levelDecreasePerBlock, float explosionResistance, int tickRate) {
			super(flowingGetter, sourceGetter, bucketGetter, blockGetter, slopeFindDistance, levelDecreasePerBlock, explosionResistance, tickRate);
		}

		@Override
		public boolean isSource(FluidState fluidState) {
			return true;
		}

		@Override
		public int getAmount(FluidState fluidState) {
			return 8;
		}
	}

	public static final class Flowing extends FabricBaseFluid {
		public Flowing(Supplier<? extends Fluid> flowingGetter, Supplier<? extends Fluid> sourceGetter,
					Supplier<? extends Item> bucketGetter, Supplier<? extends Block> blockGetter,
					int slopeFindDistance, int levelDecreasePerBlock, float explosionResistance, int tickRate) {
			super(flowingGetter, sourceGetter, bucketGetter, blockGetter, slopeFindDistance, levelDecreasePerBlock, explosionResistance, tickRate);
		}

		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public boolean isSource(FluidState fluidState) {
			return false;
		}

		@Override
		public int getAmount(FluidState fluidState) {
			return fluidState.getValue(LEVEL);
		}
	}
}
