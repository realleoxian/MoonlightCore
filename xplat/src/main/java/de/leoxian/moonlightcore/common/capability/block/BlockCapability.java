package de.leoxian.moonlightcore.common.capability.block;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

@ApiStatus.NonExtendable
public interface BlockCapability<A, C extends @Nullable Object> {
    static <A, C extends @Nullable Object> BlockCapability<A, C> get(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        return XplatAbstraction.INSTANCE.getBlockCapability(id, apiClass, contextClass);
    }

    @Nullable
    A find(Level level, BlockPos blockPos, @Nullable BlockState blockState, @Nullable BlockEntity blockEntity, C context);

    @Nullable
    default A find(Level level, BlockPos blockPos, C context) {
        return find(level, blockPos, null, null, context);
    }

    void registerForBlock(Supplier<Block> block, BlockCapability.Provider<A, C> provider);

    <BE extends BlockEntity> void registerForBlockEntity(Supplier<BlockEntityType<BE>> blockEntityType, BiFunction<BE, C, @Nullable A> provider);

    void registerSelf(Supplier<BlockEntityType<?>> blockEntityType);

    void registerFallback(BlockCapability.Provider<A, C> provider);

    BlockCapability.@Nullable Provider<A, C> getProvider(Supplier<Block> blockEntityType);

    Identifier id();

    Class<A> apiClass();

    Class<C> contextClass();

    @FunctionalInterface
    interface Provider<A, C extends @Nullable Object> {
        @Nullable
        A find(Level level, BlockPos blockPos, @Nullable BlockState blockState, @Nullable BlockEntity blockEntity, C context);
    }
}
