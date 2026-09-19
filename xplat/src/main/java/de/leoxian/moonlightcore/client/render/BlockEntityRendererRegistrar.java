package de.leoxian.moonlightcore.client.render;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface BlockEntityRendererRegistrar {
    /// Configure and register block entity renderers
    /// @param namespace The mod's id to add this registrar to
    /// @param initializer The initializer of the registrar
    static void configure(String namespace, Consumer<BlockEntityRendererRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.blockEntityRenderers(namespace, initializer);
    }

    /// Register a renderer to the given block entity type
    /// @param blockEntityType The block entity type
    /// @param provider The renderer provider
    <T extends BlockEntity, S extends BlockEntityRenderState> void register(Supplier<BlockEntityType<T>> blockEntityType, BlockEntityRendererProvider<T, S> provider);
}
