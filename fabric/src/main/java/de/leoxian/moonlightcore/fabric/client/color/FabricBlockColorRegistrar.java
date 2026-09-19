package de.leoxian.moonlightcore.fabric.client.color;

import de.leoxian.moonlightcore.client.color.BlockColorRegistrar;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Supplier;

public enum FabricBlockColorRegistrar implements BlockColorRegistrar {
    INSTANCE
    ;

    @Override
    public void register(List<BlockTintSource> tintSources, Supplier<Block> blocks) {
        BlockColorRegistry.register(tintSources, blocks.get());
    }
}
