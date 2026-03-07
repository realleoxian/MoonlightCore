package de.leoxian.moonlightcore.api.apilookup;

import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import de.leoxian.moonlightcore.impl.apilookup.BlockApiLookupImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockApiLookup<A, C extends @Nullable Object> extends ApiLookup<A, C> {

    static <A, C extends @Nullable Object> BlockApiLookup<A, C> get(ResourceLocation name, Class<A> apiClass, Class<C> contextClass) {
        return BlockApiLookupImpl.get(name, apiClass, contextClass);
    }

    @Nullable
    A get(Level level, BlockPos blockPos, @Nullable BlockState blockState, @Nullable BlockEntity blockEntity, C context);

    void register(BlockApiLookup.Provider<A, C> provider, Block... blocks);

    BlockApiLookup.@Nullable Provider<A, C> getProvider(Block block);

    @FunctionalInterface
    interface Provider<A, C extends @Nullable Object> {

        @Nullable
        A get(Level level, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, C context);

    }

}
