package de.leoxian.moonlightcore.api.fluid;

import de.leoxian.moonlightcore.impl.fluid.CauldronFluidContentImpl;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public interface CauldronFluidContent {

    static void register(Block block, Fluid fluid, int totalAmount, @Nullable IntegerProperty levelProperty) {
        CauldronFluidContentImpl.register(block, fluid, totalAmount, levelProperty);
    }

    static @Nullable CauldronFluidContent getForBlock(Block block) {
        return CauldronFluidContentImpl.getForBlock(block);
    }

    static @Nullable CauldronFluidContent getForFluid(Fluid fluid) {
        return CauldronFluidContentImpl.getForFluid(fluid);
    }

    default int currentLevel(BlockState state) {
        if (fluid() == Fluids.EMPTY) {
            return 0;
        } else if (levelProperty() == null) {
            return 1;
        }

        return state.getValue(levelProperty());
    }

    Block block();

    Fluid fluid();

    int totalAmount();

    int maxLevel();

    @Nullable IntegerProperty levelProperty();

}
