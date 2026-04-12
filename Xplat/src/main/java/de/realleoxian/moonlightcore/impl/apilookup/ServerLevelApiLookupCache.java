package de.realleoxian.moonlightcore.impl.apilookup;

import net.minecraft.core.BlockPos;

public interface ServerLevelApiLookupCache {

    void mlcore$registerBlockCache(BlockPos blockPos, BlockApiCacheImpl<?, ?> cache);

    void mlcore$invalidateBlockCache(BlockPos blockPos);

}
