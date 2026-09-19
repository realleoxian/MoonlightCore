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
    /// Creates a new block capability, or gets it if it already exists
    /// @param id The id of the capability
    /// @param apiClass Type of required API
    /// @param contextClass Type of the additional context
    /// @throws IllegalArgumentException If another `apiClass` or another `contextClass` was already registered with the same id
    static <A, C extends @Nullable Object> BlockCapability<A, C> create(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        return XplatAbstraction.INSTANCE.createBlockCapability(id, apiClass, contextClass);
    }

    /// Attempt to retrieve an instance of this capability from a block in a level.
    /// @param level The level the block is in
    /// @param blockPos The position of the block
    /// @param blockState The block state at the target position if it is known, or `null` if unknown
    /// @param blockEntity The block entity at the target position if it is known, or `null` if unknown or doesn't exist
    /// @param context Additional context for the query
    /// @return The capability instance, or `null` if it couldn't be found
    @Nullable
    A find(Level level, BlockPos blockPos, @Nullable BlockState blockState, @Nullable BlockEntity blockEntity, C context);

    /// Attempt to retrieve an instance of this capability from a block in a level.
    /// @param level The level the block is in
    /// @param blockPos The position of the block
    /// @param context Additional context for the query
    /// @return The capability instance, or `null` if it couldn't be found
    @Nullable
    default A find(Level level, BlockPos blockPos, C context) {
        return find(level, blockPos, null, null, context);
    }

    /// Registers a capability provider for a block
    /// @param block The block
    /// @param provider The capability provider
    void registerForBlock(Supplier<Block> block, BlockCapability.Provider<A, C> provider);

    /// Registers a capability provider for a block entity type
    /// @param blockEntityType The block entity type
    /// @param provider The capability provider
    <BE extends BlockEntity> void registerForBlockEntity(Supplier<BlockEntityType<BE>> blockEntityType, BiFunction<BE, C, @Nullable A> provider);

    /// Self-registers the capability from the block entity type. This can be used if then block entity itself implements the capability API
    /// @param blockEntityType The block entity type
    void registerSelf(Supplier<BlockEntityType<?>> blockEntityType);

    /// Registers a fallback provider of this capability for all blocks in registry
    /// @param provider The fallback provider
    void registerFallback(BlockCapability.Provider<A, C> provider);

    /// Retrieves the capability provider from the given block
    /// @param block The block entity
    /// @return The capability provider used on the block, or `null` if there is no capability provider registered on the block
    BlockCapability.@Nullable Provider<A, C> getProvider(Supplier<Block> block);

    /// @return This capability's id
    Identifier id();

    /// @return This capability's API type
    Class<A> apiClass();

    /// @return This capability's context type
    Class<C> contextClass();

    @FunctionalInterface
    interface Provider<A, C extends @Nullable Object> {
        @Nullable
        A find(Level level, BlockPos blockPos, @Nullable BlockState blockState, @Nullable BlockEntity blockEntity, C context);
    }
}
