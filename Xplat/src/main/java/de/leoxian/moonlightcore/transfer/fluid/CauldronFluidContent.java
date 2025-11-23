package de.leoxian.moonlightcore.transfer.fluid;

import de.leoxian.moonlightcore.lookup.ApiProviderMap;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Collection;

public final class CauldronFluidContent {
    private static final ApiProviderMap<Block, CauldronFluidContent> BLOCK_TO_CAULDRON = ApiProviderMap.create();
    private static final ApiProviderMap<Fluid, CauldronFluidContent> FLUID_TO_CAULDRON = ApiProviderMap.create();

    public static synchronized CauldronFluidContent registerCauldron(Block block, Fluid fluid, int amountPerLevel, @Nullable IntegerProperty levelProperty) {
        CauldronFluidContent existing = BLOCK_TO_CAULDRON.get(block);
        if(existing != null) {
            return existing;
        }

        if(FLUID_TO_CAULDRON.get(fluid) !=  null) {
            throw new IllegalArgumentException("Fluid already has a mapping for a different block");
        }
        CauldronFluidContent data;
        if(levelProperty == null) {
            data = new CauldronFluidContent(block, fluid, amountPerLevel, 1, null);
        } else {
            Collection<Integer> levels = levelProperty.getPossibleValues();
            if(levels.isEmpty()) {
                throw new RuntimeException("Cauldron should have at least one possible value");
            }

            int minLevel = Integer.MAX_VALUE;
            int maxLevel = 0;
            for(int level : levels) {
                minLevel = Math.min(minLevel, level);
                maxLevel = Math.max(maxLevel, level);
            }

            if(minLevel != 1) {
                throw new IllegalStateException("Minimul level should be 1, and maximum level should be >= 1");
            }

            data = new CauldronFluidContent(block, fluid, amountPerLevel, maxLevel, levelProperty);
        }

        BLOCK_TO_CAULDRON.putIfAbsent(block, data);
        FLUID_TO_CAULDRON.putIfAbsent(fluid, data);

        FluidStorage.SIDED.registerForBlocks((level, pos, state, be, ctx) -> CauldronWrapper.get(level, pos), block);

        return data;
    }

    public static CauldronFluidContent getForBlock(Block block) {
        return BLOCK_TO_CAULDRON.get(block);
    }

    public static CauldronFluidContent getForFluid(Fluid fluid) {
        return FLUID_TO_CAULDRON.get(fluid);
    }

    static {
        registerCauldron(Blocks.CAULDRON, Fluids.EMPTY, FluidConstants.BUCKET, null);
        registerCauldron(Blocks.WATER_CAULDRON, Fluids.WATER, FluidConstants.BOTTLE, LayeredCauldronBlock.LEVEL);
        registerCauldron(Blocks.LAVA_CAULDRON, Fluids.LAVA, FluidConstants.BUCKET, null);
    }

    public final Block block;
    public final Fluid fluid;
    public final int amountPerLevel;
    public final int maxLevel;
    public final @Nullable IntegerProperty levelProperty;

    private CauldronFluidContent(Block block, Fluid fluid, int amountPerLevel, int maxLevel, @Nullable IntegerProperty levelProperty) {
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
        }

        return state.getValue(levelProperty);
    }
}
