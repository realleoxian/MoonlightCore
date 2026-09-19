package de.leoxian.moonlightcore.fabric.client.render;

import de.leoxian.moonlightcore.client.render.BlockEntityRendererRegistrar;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public enum FabricBlockEntityRendererRegistrar implements BlockEntityRendererRegistrar {
    INSTANCE
    ;

    @Override
    public <T extends BlockEntity, S extends BlockEntityRenderState> void register(Supplier<BlockEntityType<T>> blockEntityType, BlockEntityRendererProvider<T, S> provider) {
        BlockEntityRenderers.register(blockEntityType.get(), provider);
    }
}
