package de.leoxian.moonlightcore.api.client.render.color;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.function.Supplier;

public interface BlockColorRegistrar {
    void registerBlockColor(BlockColor color, Supplier<Block> block);

    default void registerBlockColor(BlockColor color, Supplier<Block>... blocks) {
        Arrays.stream(blocks).forEach((sup) -> registerBlockColor(color, sup));
    }
}
