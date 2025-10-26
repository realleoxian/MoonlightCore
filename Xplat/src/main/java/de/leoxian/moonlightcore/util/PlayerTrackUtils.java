package de.leoxian.moonlightcore.util;

import de.leoxian.moonlightcore.mixin.accessor.ChunkMapAccessor;
import de.leoxian.moonlightcore.mixin.accessor.TrackedEntityAccessor;
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
 * The code of this file is from the fabric-api networking module.
 * <a href=https://github.com/FabricMC/fabric/blob/1.21.10/fabric-networking-api-v1/src/main/java/net/fabricmc/fabric/api/networking/v1/PlayerLookup.java>PlayerLookup.class</a>
 */
public final class PlayerTrackUtils {

    public static Collection<ServerPlayer> all(MinecraftServer server) {
        Objects.requireNonNull(server, "The server can't be null");

        return Collections.unmodifiableCollection(server.getPlayerList().getPlayers());
    }

    public static Collection<ServerPlayer> level(ServerLevel level) {
        Objects.requireNonNull(level, "The level can't be null");

        return Collections.unmodifiableCollection(level.players());
    }

    public static Collection<ServerPlayer> tracking(ServerLevel level, ChunkPos pos) {
        Objects.requireNonNull(level, "The level can't be null");
        Objects.requireNonNull(pos, "The chunk pos can't be null");

        return level.getChunkSource().chunkMap.getPlayers(pos, false);
    }

    public static Collection<ServerPlayer> tracking(Entity entity) {
        Objects.requireNonNull(entity, "Entity can't be null");
        ChunkSource chunkSource = entity.level().getChunkSource();

        if(chunkSource instanceof ServerChunkCache serverChunkCache) {
            ChunkMap chunkMap = serverChunkCache.chunkMap;
            TrackedEntityAccessor tracker = ((ChunkMapAccessor) chunkMap).getEntityMap().get(entity.getId());

            if(tracker != null) {
                return tracker.getSeenBy().stream().map(ServerPlayerConnection::getPlayer).collect(Collectors.toUnmodifiableSet());
            }

            return Collections.emptySet();
        }

        throw new IllegalStateException("Only supported on server-side levels!");
    }

    public static Collection<ServerPlayer> tracking(BlockEntity blockEntity) {
        Objects.requireNonNull(blockEntity, "Block entity can't be null");

        if(!blockEntity.hasLevel() || blockEntity.getLevel().isClientSide()) {
            throw new IllegalStateException("Only supported on server-side levels!");
        }

        return tracking((ServerLevel) blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    public static Collection<ServerPlayer> tracking(ServerLevel level, BlockPos pos) {
        Objects.requireNonNull(pos, "Block pos can't be null");
        return tracking(level, new ChunkPos(pos));
    }

    public static Collection<ServerPlayer> around(ServerLevel level, Vec3 pos, double radius) {
        double radiusSq = radius * radius;

        return level(level).stream()
                .filter(p -> p.distanceToSqr(pos) <= radiusSq)
                .collect(Collectors.toSet());
    }

    public static Collection<ServerPlayer> around(ServerLevel level, Vec3i pos, double radius) {
        double radiusSq = radius * radius;

        return level(level)
                .stream()
                .filter(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= radiusSq)
                .collect(Collectors.toList());
    }

    private PlayerTrackUtils() {
    }

}
