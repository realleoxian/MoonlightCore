package de.realleoxian.moonlightcore.impl.fluid;

import de.realleoxian.moonlightcore.api.fluid.CauldronFluidContent;
import de.realleoxian.moonlightcore.api.fluid.FluidConstants;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

public record CauldronFluidContentImpl(Block block, Fluid fluid, int totalAmount, int maxLevel, @Nullable IntegerProperty levelProperty) implements CauldronFluidContent {
    private static final Map<Block, CauldronFluidContent> BLOCK_TO_CAULDRON = new IdentityHashMap<>();
    private static final Map<Fluid, CauldronFluidContent> FLUID_TO_CAULDRON = new IdentityHashMap<>();

    static  {
        register(Blocks.CAULDRON, Fluids.EMPTY, FluidConstants.BUCKET, null);
        register(Blocks.WATER_CAULDRON, Fluids.WATER, FluidConstants.BOTTLE, LayeredCauldronBlock.LEVEL);
        register(Blocks.LAVA_CAULDRON, Fluids.LAVA, FluidConstants.BUCKET, null);
    }

    @ApiStatus.Internal
    public static void init() {
        // no-op
    }

    public static void register(Block block, Fluid fluid, int totalAmount, @Nullable IntegerProperty levelProperty) {
        if (BLOCK_TO_CAULDRON.get(block) != null) {
            throw new IllegalArgumentException("Duplicated cauldron registration for block %s".formatted(block));
        }
        if (FLUID_TO_CAULDRON.get(fluid) != null) {
            throw new IllegalArgumentException("Duplicated cauldron registration for fluid %s".formatted(fluid));
        }
        if(totalAmount <= 0) {
            throw new IllegalArgumentException("Cauldron total amount %d should be positive".formatted(totalAmount));
        }

        CauldronFluidContent content;
        if (levelProperty == null) {
            content = new CauldronFluidContentImpl(block, fluid, totalAmount, 1, null);
        } else {
            Collection<Integer> levels = levelProperty.getPossibleValues();
            if (levels.isEmpty()) {
                throw new IllegalArgumentException("Cauldron should have at least one possible level");
            }

            int minLevel = Integer.MAX_VALUE;
            int maxLevel = 0;

            for (int level : levels) {
                minLevel = Math.min(minLevel, level);
                maxLevel = Math.max(maxLevel, level);
            }

            if (minLevel != 1) {
                throw new IllegalStateException("Minimum level should be 1, and maximum should be >= 1");
            }
            content = new CauldronFluidContentImpl(block, fluid, totalAmount, maxLevel, levelProperty);
        }

        BLOCK_TO_CAULDRON.put(block, content);
        FLUID_TO_CAULDRON.put(fluid, content);
    }

    public static @Nullable CauldronFluidContent getForBlock(Block block) {
        return BLOCK_TO_CAULDRON.get(block);
    }

    public static @Nullable CauldronFluidContent getForFluid(Fluid fluid) {
        return FLUID_TO_CAULDRON.get(fluid);
    }

}
