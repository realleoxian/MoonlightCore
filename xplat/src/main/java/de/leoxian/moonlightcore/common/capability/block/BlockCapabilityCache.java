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
    static <A, C extends @Nullable Object> BlockCapabilityCache<A, C> get(BlockCapability<A, C> capability, ServerLevel level, BlockPos blockPos, C context) {
        return XplatAbstraction.INSTANCE.getBlockCapabilityCache(capability, level, blockPos, context);
    }

    @Nullable
    A find(@Nullable BlockState blockState);

    @Nullable BlockEntity blockEntity();

    BlockCapability<A, C> capability();

    ServerLevel level();

    BlockPos blockPos();
}
