package de.leoxian.moonlightcore.registry.builder;

import de.leoxian.moonlightcore.event.client.RenderingEvents;
import de.leoxian.moonlightcore.platform.EnvironmentSide;
import de.leoxian.moonlightcore.registry.DeferredRegistrar;
import de.leoxian.moonlightcore.util.nullness.NonnullFunction;
import de.leoxian.moonlightcore.util.nullness.NonnullSupplier;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BlockEntityBuilder<T extends BlockEntity> extends AbstractBuilder<BlockEntityType<?>, BlockEntityType<T>, BlockEntityBuilder<T>> {

    public static <T extends BlockEntity> BlockEntityBuilder<T> builder(DeferredRegistrar<BlockEntityType<?>> registrar, String name, BlockEntityFactory<T> factory) {
        return new BlockEntityBuilder<>(registrar, name, factory);
    }

    private final BlockEntityFactory<T> factory;
    private final Set<NonnullSupplier<? extends Block>> validBlocks = new HashSet<>();

    private @Nullable NonnullSupplier<NonnullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<T>>> renderer = null;

    protected BlockEntityBuilder(DeferredRegistrar<BlockEntityType<?>> registrar, String name, BlockEntityFactory<T> factory) {
        super(registrar, name);
        this.factory = factory;
    }

    public BlockEntityBuilder<T> validBlock(NonnullSupplier<? extends Block> block) {
        this.validBlocks.add(block);
        return this;
    }

    @SafeVarargs
    public final BlockEntityBuilder<T> validBlocks(NonnullSupplier<? extends Block>... blocks) {
        Arrays.stream(blocks).forEach(this::validBlock);
        return this;
    }

    public BlockEntityBuilder<T> renderer(NonnullSupplier<NonnullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<T>>> renderer) {
        if(this.renderer == null) EnvironmentSide.CLIENT.runIfCurrent(() -> this::setupRenderer);
        this.renderer = renderer;
        return this;
    }

    @Override
    protected BlockEntityType<T> buildEntry() {
        return BlockEntityType.Builder.of((pos, state) -> factory.create(getValue(), pos, state), validBlocks.stream().map(NonnullSupplier::get).toArray(Block[]::new)).build(null);
    }

    private void setupRenderer() {
        var renderer = this.renderer;

        if(renderer != null) {
            RenderingEvents.RENDERER_REGISTRATION.subscribe(output -> output.registerBlockEntity(getValue(), renderer.get()::apply));
        }
    }

    public interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockEntityType<T> type, BlockPos blockPos, BlockState blockState);
    }

}
