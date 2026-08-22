package de.leoxian.moonlightcore.neoforge.client.color;

import de.leoxian.moonlightcore.client.color.BlockColorRegistrar;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.List;
import java.util.function.Supplier;

public record NeoforgeBlockColorRegistrar(RegisterColorHandlersEvent.BlockTintSources event) implements BlockColorRegistrar {
    @Override
    public void register(List<BlockTintSource> tintSources, Supplier<Block> blocks) {
        event.register(tintSources, blocks.get());
    }
}
