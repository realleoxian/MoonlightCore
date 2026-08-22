package de.leoxian.moonlightcore.neoforge.client.render;

import de.leoxian.moonlightcore.client.render.BlockEntityRendererRegistrar;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.function.Supplier;

public record NeoforgeBlockEntityRendererRegistrar(EntityRenderersEvent.RegisterRenderers event) implements BlockEntityRendererRegistrar {
    @Override
    public <T extends BlockEntity, S extends BlockEntityRenderState> void register(Supplier<BlockEntityType<T>> blockEntityType, BlockEntityRendererProvider<T, S> provider) {
        event.registerBlockEntityRenderer(blockEntityType.get(), provider);
    }
}
