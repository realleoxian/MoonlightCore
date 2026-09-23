package de.leoxian.moonlightcore.client.color;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface BlockColorRegistrar {
	/// Configure and register tint sources for blocks
	/// @param namespace The mod's id to add this registrar to
	/// @param initializer The initializer of the registrar
	static void init(String namespace, Consumer<BlockColorRegistrar> initializer) {
		XplatClientAbstraction.INSTANCE.blockColor(namespace, initializer);
	}

	/// Registers a new list of block tint sources to the given  block
	/// @param tintSources The tint sources for the block
	/// @param blocks The block
	void register(List<BlockTintSource> tintSources, Supplier<Block> blocks);
}
