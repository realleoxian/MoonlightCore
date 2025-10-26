package de.leoxian.moonlightcore.registry.builder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class BlockBuilder<T extends Block> extends AbstractBuilder<Block, T> {
    public static <T extends Block> BlockBuilder<T> of(ResourceLocation id, Function<BlockBehaviour.Properties, T> blockFactory) {
        return new BlockBuilder<>(id, blockFactory);
    }

    private final Function<BlockBehaviour.Properties, T> blockFactory;

    private Supplier<BlockBehaviour.Properties> initialProperties = BlockBehaviour.Properties::of;
    private Function<BlockBehaviour.Properties, BlockBehaviour.Properties> propertiesCallback = UnaryOperator.identity();

    protected BlockBuilder(ResourceLocation id, Function<BlockBehaviour.Properties, T> blockFactory) {
        super(Registries.BLOCK, id);
        this.blockFactory = blockFactory;
    }

    public BlockBuilder<T> initialProperties(Supplier<BlockBehaviour.Properties> initialProperties) {
        this.initialProperties = initialProperties;
        return this;
    }

    public BlockBuilder<T> initialProperties(BlockBehaviour block) {
        return this.initialProperties(() -> BlockBehaviour.Properties.copy(block));
    }

    public BlockBuilder<T> properties(UnaryOperator<BlockBehaviour.Properties> callback) {
        this.propertiesCallback = propertiesCallback.andThen(callback);
        return this;
    }

    @Override
    protected T buildEntry() {
        BlockBehaviour.Properties properties = this.initialProperties.get();
        properties = this.propertiesCallback.apply(properties);

        return this.blockFactory.apply(properties);
    }
}
