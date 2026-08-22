package de.leoxian.moonlightcore.client.render;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface BlockEntityRendererRegistrar {
    static void init(String namespace, Consumer<BlockEntityRendererRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.blockEntityRenderers(namespace, initializer);
    }

    <T extends BlockEntity, S extends BlockEntityRenderState> void register(Supplier<BlockEntityType<T>> blockEntityType, BlockEntityRendererProvider<T, S> provider);
}
