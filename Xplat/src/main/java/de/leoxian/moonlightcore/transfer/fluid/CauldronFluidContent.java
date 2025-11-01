package de.leoxian.moonlightcore.transfer.fluid;

import de.leoxian.moonlightcore.lookup.ApiProviderMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class CauldronFluidContent {
    private static final ApiProviderMap<Block, CauldronFluidContent> BLOCK_TO_CAULDRON = ApiProviderMap.create();
    private static final ApiProviderMap<Fluid, CauldronFluidContent> FLUID_TO_CAULDRON = ApiProviderMap.create();

    static {
        CauldronFluidContent.registerCauldron(Blocks.CAULDRON, Fluids.EMPTY, FluidConstants.BUCKET, null);
        CauldronFluidContent.registerCauldron(Blocks.WATER_CAULDRON, Fluids.WATER, FluidConstants.BOTTLE, LayeredCauldronBlock.LEVEL);
        CauldronFluidContent.registerCauldron(Blocks.LAVA_CAULDRON, Fluids.LAVA, FluidConstants.BUCKET, null);
    }

    public static synchronized CauldronFluidContent registerCauldron(Block block, Fluid fluid, int amountPerLevel, @Nullable IntegerProperty levelProperty) {
        CauldronFluidContent existing = getForBlock(block);

        if(existing != null) {
            return existing;
        }

        if(FLUID_TO_CAULDRON.get(fluid) != null) {
            throw new IllegalArgumentException("Fluid already has a mapping for a different block");
        }

        CauldronFluidContent data;
        if(levelProperty == null) {
            data = new CauldronFluidContent(block, fluid, amountPerLevel, 1, null);
        } else {
            Collection<Integer> levels = levelProperty.getPossibleValues();

            if(levels.isEmpty()) {
                throw new RuntimeException("Cauldron should have at least one possible level");
            }

            int minLevel = Integer.MAX_VALUE;
            int maxLevel = 0;

            for(int level : levels) {
                minLevel = Math.min(minLevel, level);
                maxLevel = Math.max(maxLevel, level);
            }

            if(minLevel != 1) {
                throw new IllegalArgumentException("Minimum level should be 1, and maximum level should be greater than 1");
            }

            data = new CauldronFluidContent(block, fluid, amountPerLevel, maxLevel, levelProperty);
        }

        BLOCK_TO_CAULDRON.putIfAbsent(block, data);
        FLUID_TO_CAULDRON.putIfAbsent(fluid, data);

        FluidStorage.SIDED.registerForBlocks((level, pos, state, be, ctx) -> CauldronWrapper.of(level, pos), block);
        return data;
    }

    @Nullable
    public static CauldronFluidContent getForBlock(Block block) {
        return BLOCK_TO_CAULDRON.get(block);
    }

    @Nullable
    public static CauldronFluidContent getForFluid(Fluid fluid) {
        return FLUID_TO_CAULDRON.get(fluid);
    }

    public final Block block;
    public final Fluid fluid;
    public final int amountPerLevel;
    public final int maxLevel;

    @Nullable
    public final IntegerProperty levelProperty;

    CauldronFluidContent(Block block, Fluid fluid, int amountPerLevel, int maxLevel, @Nullable IntegerProperty levelProperty) {
        this.block = block;
        this.fluid = fluid;
        this.amountPerLevel = amountPerLevel;
        this.maxLevel = maxLevel;
        this.levelProperty = levelProperty;
    }

    public int currentLevel(BlockState state) {
        if(this.fluid == Fluids.EMPTY) {
            return 0;
        } else if(levelProperty == null) {
            return 1;
        } else {
            return state.getValue(this.levelProperty);
        }
    }
}
