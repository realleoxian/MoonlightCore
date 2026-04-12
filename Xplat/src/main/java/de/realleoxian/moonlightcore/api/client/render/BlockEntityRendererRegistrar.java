package de.realleoxian.moonlightcore.api.client.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface BlockEntityRendererRegistrar {
    <BE extends BlockEntity> void register(BlockEntityType<BE> blockEntityType, BlockEntityRendererProvider<BE> provider);
}
