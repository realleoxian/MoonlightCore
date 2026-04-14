package de.realleoxian.moonlightcore.api.client.model;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockStateModelModifier {
    void applyBlockStateModels(Context context);

    interface Context {
        void replace(BlockState state, BakedModel model);

        default void replaceAllStates(BakedModel model) {
            getBlock().getStateDefinition().getPossibleStates().forEach(state -> replace(state, model));
        }

        Block getBlock();
    }
}
