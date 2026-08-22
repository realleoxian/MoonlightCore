package de.leoxian.moonlightcore.client.color;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface BlockColorRegistrar {
    static void init(String namespace, Consumer<BlockColorRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.blockColor(namespace, initializer);
    }

    void register(List<BlockTintSource> tintSources, Supplier<Block> blocks);
}
