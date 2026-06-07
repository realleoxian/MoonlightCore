package de.realleoxian.moonlightcore.api.client.render;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.function.Supplier;

@ApiStatus.NonExtendable
public interface BlockColorHandlerRegistrar {
    void registerBlockColor(BlockColor color, Supplier<Block> block);

    default void registerBlockColor(BlockColor color, Supplier<Block>... blocks) {
        Arrays.stream(blocks).forEach((sup) -> registerBlockColor(color, sup));
    }
}
