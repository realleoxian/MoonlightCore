package de.realleoxian.moonlightcore.api.client.render;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.function.Supplier;

public interface ChunkRenderLayerRegistrar {
    void register(RenderType renderType, Supplier<Block> block);

    default void register(RenderType renderType, Supplier<Block>... blocks) {
        Arrays.stream(blocks).forEach(block -> register(renderType, block));
    }
}
