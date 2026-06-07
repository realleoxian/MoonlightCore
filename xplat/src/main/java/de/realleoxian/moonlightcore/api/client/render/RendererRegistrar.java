package de.realleoxian.moonlightcore.api.client.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public interface RendererRegistrar {
    <BE extends BlockEntity> void register(Supplier<BlockEntityType<BE>> blockEntityType, BlockEntityRendererProvider<BE> provider);

    <E extends Entity> void register(Supplier<EntityType<E>> entityType, EntityRendererProvider<E> provider);
}
