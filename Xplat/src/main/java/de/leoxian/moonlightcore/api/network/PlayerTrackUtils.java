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
package de.leoxian.moonlightcore.api.network;

import de.leoxian.moonlightcore.mixin.ChunkMapAccessor;
import de.leoxian.moonlightcore.mixin.TrackedEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Helper methods to lookup players in a server.
 *
 * <p>The word "tracking" means that an entity/chunk on the server is known to a player's client (within in view distance) and the (block) entity should notify tracking clients of changes.
 *
 * <p>These methods should only be called on the server thread and only be used on logical a server.
 */
public final class PlayerTrackUtils {

    public static Collection<ServerPlayer> all(MinecraftServer server) {
        Objects.requireNonNull(server, "The server cannot be 'null'");

        return Collections.unmodifiableCollection(server.getPlayerList().getPlayers());
    }

    public static Collection<ServerPlayer> level(ServerLevel level) {
        Objects.requireNonNull(level, "The level cannot be 'null'");

        return Collections.unmodifiableCollection(level.players());
    }

    public static Collection<ServerPlayer> tracking(ServerLevel level, ChunkPos chunkPos) {
        Objects.requireNonNull(level, "The level cannot be 'null'");
        Objects.requireNonNull(chunkPos, "The chunk pos cannot be 'null'");

        return level.getChunkSource().chunkMap.getPlayers(chunkPos, false);
    }

    public static Collection<ServerPlayer> tracking(Entity entity) {
        Objects.requireNonNull(entity, "Entity cannot be 'null'");
        ChunkSource chunkSource = entity.level().getChunkSource();

        if(chunkSource instanceof ServerChunkCache cache) {
            ChunkMap chunkMap = cache.chunkMap;
            TrackedEntityAccessor trackedEntity = ((ChunkMapAccessor) chunkMap).getEntityMap().get(entity.getId());

            if(trackedEntity != null) {
                return trackedEntity.getSeenBy().stream().map(ServerPlayerConnection::getPlayer).collect(Collectors.toUnmodifiableSet());
            }

            return Collections.emptySet();
        }

        throw new IllegalArgumentException("Only supported on server levels!");
    }

    public static Collection<ServerPlayer> tracking(BlockEntity blockEntity) {
        Objects.requireNonNull(blockEntity, "BlockEntity cannot be 'null'");

        if(blockEntity.hasLevel() && blockEntity.getLevel().isClientSide()) {
            throw new IllegalArgumentException("Only supported on server levels!");
        }

        return tracking((ServerLevel) blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    public static Collection<ServerPlayer> tracking(ServerLevel level, BlockPos blockPos) {
        Objects.requireNonNull(blockPos, "BlockPos cannot be 'null'");

        return tracking(level, new ChunkPos(blockPos));
    }

    public static Collection<ServerPlayer> around(ServerLevel level, Vec3 pos, double radius) {
        double radiusSq = radius * radius;
        return level(level).stream().filter(p -> p.distanceToSqr(pos) <= radiusSq).toList();
    }

    public static Collection<ServerPlayer> around(ServerLevel level, Vec3i pos, double radius) {
        double radiusSq = radius * radius;
        return level(level).stream().filter(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= radiusSq).toList();
    }

    private PlayerTrackUtils() {}
}
