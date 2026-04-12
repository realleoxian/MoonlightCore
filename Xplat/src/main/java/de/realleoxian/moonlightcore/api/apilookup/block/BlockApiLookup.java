package de.realleoxian.moonlightcore.api.apilookup.block;

import de.realleoxian.moonlightcore.api.apilookup.ApiLookup;
import de.realleoxian.moonlightcore.impl.apilookup.BlockApiLookupImpl;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface BlockApiLookup<A, C extends @Nullable Object> extends ApiLookup<A, C> {
    static <A, C extends @Nullable Object> BlockApiLookup<A, C> find(ResourceLocation name, Class<A> apiClass, Class<C> contextClass) {
        return BlockApiLookupImpl.find(name, apiClass, contextClass);
    }

    @Nullable
    A find(Level level, BlockPos blockPos, @Nullable BlockState blockState, @Nullable BlockEntity blockEntity, C context);

    void register(BlockApiLookup.Provider<A, C> provider, Block... blocks);

    void registerFallback(BlockApiLookup.Provider<A, C> fallback);

    BlockApiLookup.@Nullable Provider<A, C> getProvider(Block block);

    List<Provider<A, C>> getFallbackProviders();

    @FunctionalInterface
    interface Provider<A, C extends @Nullable Object> {
        @Nullable
        A get(Level level, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, C context);
    }
}

