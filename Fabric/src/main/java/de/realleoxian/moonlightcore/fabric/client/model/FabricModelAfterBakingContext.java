package de.realleoxian.moonlightcore.fabric.client.model;

import de.realleoxian.moonlightcore.api.client.event.ModelEvents;
import de.realleoxian.moonlightcore.api.client.model.BlockStateModelModifier;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

public record FabricModelAfterBakingContext(ModelBakery modelBakery) implements ModelEvents.AfterBaking.Context {
    @Override
    public void setModel(ResourceLocation location, BakedModel model) {
        modelBakery.getBakedTopLevelModels().put(location, model);
    }

    @Override
    public void modifyBlockStateModels(Block block, BlockStateModelModifier modelModifier) {
        modelModifier.applyBlockStateModels(new BlockStateModelModifier.Context() {
            @Override
            public void replace(BlockState state, BakedModel model) {
                if (!getBlock().getStateDefinition().getPossibleStates().contains(state)) {
                    throw new IllegalArgumentException("BlockState %s isn't present on block %s".formatted(state, BuiltInRegistries.BLOCK.getKey(getBlock())));
                }

                ModelResourceLocation location = BlockModelShaper.stateToModelLocation(state);
                setModel(location, model);
            }

            @Override
            public Block getBlock() {
                return block;
            }
        });
    }

    @Override
    public ModelBakery getModelBakery() {
        return modelBakery;
    }

    @Override
    public @UnmodifiableView Map<ResourceLocation, BakedModel> getModels() {
        return Map.copyOf(modelBakery.getBakedTopLevelModels());
    }
}
