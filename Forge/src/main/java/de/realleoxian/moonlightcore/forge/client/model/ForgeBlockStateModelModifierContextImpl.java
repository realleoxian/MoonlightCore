package de.realleoxian.moonlightcore.forge.client.model;

import de.realleoxian.moonlightcore.api.client.model.BlockStateModelModifier;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public record ForgeBlockStateModelModifierContextImpl(Block block, Map<ResourceLocation, BakedModel> models) implements BlockStateModelModifier.Context {
    @Override
    public void replace(BlockState state, BakedModel model) {
        if (!getBlock().getStateDefinition().getPossibleStates().contains(state)) {
            throw new IllegalArgumentException("BlockState %s isn't present on block %s".formatted(state, BuiltInRegistries.BLOCK.getKey(getBlock())));
        }

        ModelResourceLocation location = BlockModelShaper.stateToModelLocation(state);
        models.put(location, model);
    }

    @Override
    public Block getBlock() {
        return block;
    }
}
