package de.leoxian.moonlightcore.neoforge.common.capability;

import de.leoxian.moonlightcore.common.capability.block.BlockCapability;
import de.leoxian.moonlightcore.common.capability.block.BlockCapabilityCache;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class NeoforgeBlockCapabilityCache<A, C> implements BlockCapabilityCache<A, C> {
    private final ServerLevel level;
    private final BlockPos blockPos;
    private final BlockCapability<A, C> capability;
    private final net.neoforged.neoforge.capabilities.BlockCapabilityCache<A, C> innerCache;

    public NeoforgeBlockCapabilityCache(ServerLevel level, BlockPos blockPos, BlockCapability<A, C> capability, C context) {
        this.level = level;
        this.blockPos = blockPos;
        this.capability = capability;

        this.innerCache = net.neoforged.neoforge.capabilities.BlockCapabilityCache.create(
                ((NeoforgeBlockCapability<A, C>) capability).neoCapability,
                level,
                blockPos,
                context
        );
    }

    @Override
    public @Nullable A find(@Nullable BlockState blockState) {
        return innerCache.getCapability();
    }

    @Override
    public @Nullable BlockEntity blockEntity() {
        return this.level.getBlockEntity(blockPos);
    }

    @Override
    public BlockCapability<A, C> capability() {
        return this.capability;
    }

    @Override
    public ServerLevel level() {
        return this.level;
    }

    @Override
    public BlockPos blockPos() {
        return this.blockPos;
    }
}