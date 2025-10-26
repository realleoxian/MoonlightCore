package de.leoxian.moonlightcore.registry.builder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BlockEntityBuilder<T extends BlockEntity> extends AbstractBuilder<BlockEntityType<?>, BlockEntityType<T>> {
    public static <T extends BlockEntity> BlockEntityBuilder<T> of(ResourceLocation id, BlockEntityType.BlockEntitySupplier<T> factory) {
        return new BlockEntityBuilder<>(id, factory);
    }

    private final BlockEntityType.BlockEntitySupplier<T> factory;
    private final Set<Block> validBlocks = new HashSet<>();

    protected BlockEntityBuilder(ResourceLocation id, BlockEntityType.BlockEntitySupplier<T> factory) {
        super(Registries.BLOCK_ENTITY_TYPE, id);
        this.factory = factory;
    }

    public BlockEntityBuilder<T> validBlocks(Block... blocks) {
        this.validBlocks.addAll(Arrays.asList(blocks));
        return this;
    }

    @Override
    protected BlockEntityType<T> buildEntry() {
        return BlockEntityType.Builder.of(this.factory, this.validBlocks.toArray(new Block[0])).build(null);
    }
}
