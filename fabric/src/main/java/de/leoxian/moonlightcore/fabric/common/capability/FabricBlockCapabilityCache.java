package de.leoxian.moonlightcore.fabric.common.capability;

import de.leoxian.moonlightcore.common.capability.block.BlockCapability;
import de.leoxian.moonlightcore.common.capability.block.BlockCapabilityCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class FabricBlockCapabilityCache<A, C> implements BlockCapabilityCache<A, C> {
    private final BlockCapability<A, C> capability;
    private final BlockApiCache<A, C> capabilityCache;
    private final C context;

    public FabricBlockCapabilityCache(BlockCapability<A, C> capability, ServerLevel level, BlockPos blockPos, C context) {
        if (!(capability instanceof FabricBlockCapability<A,C> fabricBlockCapability)) {
            throw new IllegalStateException("Cannot use fabric block capability cache with a foreign implementation");
        }

        this.capability = capability;
        this.capabilityCache = BlockApiCache.create(fabricBlockCapability.apiLookup, level, blockPos);
        this.context = context;
    }

    @Override
    public @Nullable A find(@Nullable BlockState blockState) {
        return this.capabilityCache.find(blockState, this.context);
    }

    @Override
    public @Nullable BlockEntity blockEntity() {
        return this.capabilityCache.getBlockEntity();
    }

    @Override
    public BlockCapability<A, C> capability() {
        return this.capability;
    }

    @Override
    public ServerLevel level() {
        return capabilityCache.getLevel();
    }

    @Override
    public BlockPos blockPos() {
        return capabilityCache.getPos();
    }

}
