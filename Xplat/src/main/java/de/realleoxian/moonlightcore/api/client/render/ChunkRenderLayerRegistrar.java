package de.realleoxian.moonlightcore.api.client.render;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.function.Supplier;

public interface ChunkRenderLayerRegistrar {
    void registerBlock(RenderType renderType, Supplier<Block> block);

    default void registerBlock(RenderType renderType, Supplier<Block>... blocks) {
        Arrays.stream(blocks).forEach(block -> registerBlock(renderType, block));
    }

    void registerFluid(RenderType renderType, Supplier<Fluid> fluid);

    default void registerFluid(RenderType renderType, Supplier<Fluid>... fluids) {
        Arrays.stream(fluids).forEach(fluid -> registerFluid(renderType, fluid));
    }
}
