package de.leoxian.moonlightcore.common.capability.block;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface BlockCapabilityCache<A, C extends @Nullable Object> {
    /// Create a new instance bound to the passed [ServerLevel] and position, and querying the same capability as the passed lookup
    /// @param capability The capability this cache will be bound to
    /// @param level The level the block is in
    /// @param blockPos The position of the target block
    /// @param context Additional context for the queries
    static <A, C extends @Nullable Object> BlockCapabilityCache<A, C> create(BlockCapability<A, C> capability, ServerLevel level, BlockPos blockPos, C context) {
        return XplatAbstraction.INSTANCE.createBlockCapabilityCache(capability, level, blockPos, context);
    }

    /// Attempt to retrieve an instance of the capability bound to this cache
    /// @param blockState The block state of the target block if known, or `null` if unknown
    /// @return The capability instance or `null` if it couldn't be found
    @Nullable
    A find(@Nullable BlockState blockState);

    /// @return The block entity at the target position of this lookup
    @Nullable BlockEntity blockEntity();

    /// @return The capability this cache is bound to
    BlockCapability<A, C> capability();

    /// @return The level this cache its bound to
    ServerLevel level();

    /// @return The position this cache its bound to
    BlockPos blockPos();
}
