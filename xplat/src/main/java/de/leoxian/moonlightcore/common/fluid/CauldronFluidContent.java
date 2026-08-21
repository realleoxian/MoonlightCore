package de.leoxian.moonlightcore.common.fluid;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class CauldronFluidContent {
    private static final Map<Block, CauldronFluidContent> BLOCK_TO_CAULDRON = new IdentityHashMap<>();
    private static final Map<Fluid, CauldronFluidContent> FLUID_TO_CAULDRON = new IdentityHashMap<>();

    public static synchronized void register(Supplier<Block> block, Supplier<Fluid> fluid, int amountPerLevel, @Nullable IntegerProperty levelProperty) {
        if (BLOCK_TO_CAULDRON.containsKey(block.get())) throw new IllegalArgumentException("Block has a mapping for a different cauldron content");
        if (FLUID_TO_CAULDRON.containsKey(fluid.get())) throw new IllegalArgumentException("Fluid has a mapping for a different cauldron content");

        CauldronFluidContent content = null;
        if (levelProperty == null) {
            content = new CauldronFluidContent(block.get(), fluid.get(), amountPerLevel, 1, null);
        } else {
            var levels = levelProperty.getPossibleValues();
            if (levels.isEmpty()) {
                throw new RuntimeException("Cauldron should have at least one possible level");
            }

            int minLevel = Integer.MAX_VALUE;
            int maxLevel = 0;
            for (int level : levels) {
                minLevel = Math.min(minLevel, level);
                maxLevel = Math.max(maxLevel, level);
            }

            if (maxLevel < 1 || minLevel != 1) {
                throw new IllegalStateException("Minimum level should be 1, and maximum level should be >= 1");
            }

            content = new CauldronFluidContent(block.get(), fluid.get(), amountPerLevel, maxLevel, levelProperty);
        }
        BLOCK_TO_CAULDRON.put(block.get(), content);
        FLUID_TO_CAULDRON.put(fluid.get(), content);
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

    private CauldronFluidContent(Block block, Fluid fluid, int amountPerLevel, int maxLevel, @Nullable IntegerProperty levelProperty) {
        this.block = block;
        this.fluid = fluid;
        this.amountPerLevel = amountPerLevel;
        this.maxLevel = maxLevel;
        this.levelProperty = levelProperty;
    }

    public int currentLevel(BlockState blockState) {
        if (this.fluid == Fluids.EMPTY) return 0;
        else if (this.levelProperty == null) return 1;
        return blockState.getValue(this.levelProperty);
    }
}
