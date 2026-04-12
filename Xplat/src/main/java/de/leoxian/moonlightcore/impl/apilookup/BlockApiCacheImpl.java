/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.leoxian.moonlightcore.impl.apilookup;

import de.leoxian.moonlightcore.api.apilookup.block.BlockApiCache;
import de.leoxian.moonlightcore.api.apilookup.block.BlockApiLookup;
import de.leoxian.moonlightcore.api.event.EventPriority;
import de.leoxian.moonlightcore.api.event.ServerBlockEntityEvents;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockApiCacheImpl<A, C extends @Nullable Object> implements BlockApiCache<A, C> {

    static {
        ServerBlockEntityEvents.LOAD.subscribe(EventPriority.HIGHEST, (level, blockEntity) ->
                ((ServerLevelApiLookupCache) level).mlcore$invalidateBlockCache(blockEntity.getBlockPos()));

        ServerBlockEntityEvents.UNLOAD.subscribe(EventPriority.HIGHEST, (level, blockEntity) ->
                ((ServerLevelApiLookupCache) level).mlcore$invalidateBlockCache(blockEntity.getBlockPos()));
    }

    private final BlockApiLookup<A, C> lookup;
    private final ServerLevel level;
    private final BlockPos blockPos;

    private @Nullable BlockEntity cachedBlockEntity = null;
    private boolean blockEntityCacheValid = false;
    private BlockState lastBlockState = null;
    private BlockApiLookup.Provider<A, C> apiProvider = null;

    public BlockApiCacheImpl(BlockApiLookup<A, C> lookup, ServerLevel level, BlockPos blockPos) {
        ((ServerLevelApiLookupCache) level).mlcore$registerBlockCache(blockPos, this);

        this.lookup = lookup;
        this.level = level;
        this.blockPos = blockPos.immutable();
    }

    public void invalidate() {
        this.blockEntityCacheValid = false;
        this.cachedBlockEntity = null;
        this.lastBlockState = null;
        this.apiProvider = null;
    }

    @Override
    public @Nullable A get(@Nullable BlockState blockState, C context) {
        getBlockEntity();

        if(blockState == null) {
            if(blockEntityCacheValid) {
                blockState = cachedBlockEntity.getBlockState();
            } else {
                blockState = level.getBlockState(blockPos);
            }
        }

        if(lastBlockState != blockState) {
            apiProvider = lookup.getProvider(blockState.getBlock());
            lastBlockState = blockState;
        }

        A instance = null;
        if(apiProvider != null) {
            instance = apiProvider.get(level, blockPos, blockState, cachedBlockEntity, context);
        }

        if(instance == null) {
            for(BlockApiLookup.Provider<A, C> fallback : lookup.getFallbackProviders()) {
                instance = fallback.get(level, blockPos, blockState, cachedBlockEntity, context);

                if(instance != null) {
                    break;
                }
            }
        }

        return instance;
    }

    @Override
    public BlockApiLookup<A, C> getLookup() {
        return lookup;
    }

    @Override
    public ServerLevel getLevel() {
        return level;
    }

    @Override
    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public @Nullable BlockEntity getBlockEntity() {
        if(!blockEntityCacheValid) {
            cachedBlockEntity = level.getBlockEntity(blockPos);
            blockEntityCacheValid = true;
        }

        return cachedBlockEntity;
    }
}
