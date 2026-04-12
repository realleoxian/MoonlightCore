package de.leoxian.moonlightcore.api.apilookup.block;

import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockApiCache<A, C extends @Nullable Object> {
    @Nullable
    A get(@Nullable BlockState blockState, C context);

    @Nullable
    default A get(C context) {
        return get(null, context);
    }

    BlockApiLookup<A, C> getLookup();

    ServerLevel getLevel();

    BlockPos getBlockPos();

    @Nullable
    BlockEntity getBlockEntity();
}

