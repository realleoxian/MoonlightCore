package de.leoxian.moonlightcore.client.color;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Supplier;

public interface BlockColorRegistrar {
    void register(List<BlockTintSource> tintSources, Supplier<Block[]> blocks);

    void register(List<BlockTintSource> tintSources, Iterable<? extends Block> blocks);
}
