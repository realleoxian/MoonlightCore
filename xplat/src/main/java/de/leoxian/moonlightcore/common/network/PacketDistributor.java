package de.leoxian.moonlightcore.common.network;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PacketDistributor {
	/// Sends a packet to a player
	/// @param player The player
	/// @param payload The main packet payload
	/// @param payloads Other packet payloads that can be sent with the main one
	public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload, CustomPacketPayload... payloads) {
		player.connection.send(makeClientboundPacket(payload, payloads));
	}

	/// Sends a packet to all players present on the given level
	/// @param level The level the players are in
	/// @param payload The main packet payload
	/// @param payloads Other packet payloads that can be sent with the main one
	public static void sendToPlayersInDimension(ServerLevel level, CustomPacketPayload payload, CustomPacketPayload... payloads) {
		level.getServer().getPlayerList().broadcastAll(makeClientboundPacket(payload, payloads), level.dimension());
	}

	/// Sends a packet to all the players near a specific position
	/// @param level The level the players are in
	/// @param excluded A player that may be excluded from that packet sending, may be `null`
	/// @param x The `x` target position
	/// @param y The `y` target position
	/// @param z The `z` target position
	/// @param radius The radius to check players are in
	/// @param payload The main packet payload
	/// @param payloads Other packet payloads that can be sent with the main one
	public static void sendToPlayersNear(ServerLevel level, @Nullable ServerPlayer excluded, double x, double y, double z, double radius, CustomPacketPayload payload, CustomPacketPayload... payloads) {
		Packet<?> packet = makeClientboundPacket(payload, payloads);
		level.getServer().getPlayerList().broadcast(excluded, x, y, z, radius, level.dimension(), packet);
	}

	/// Sends a packet to all the players on the server
	/// @param payload The main packet payload
	/// @param payloads Other packet payloads that can be sent with the main one
	public static void sendToAllPlayers(CustomPacketPayload payload, CustomPacketPayload... payloads) {
		MinecraftServer server = Objects.requireNonNull(XplatAbstraction.INSTANCE.getCurrentServer(), "Cannot send clientbound payloads on the client");
		server.getPlayerList().broadcastAll(makeClientboundPacket(payload, payloads));
	}

	/// Sends a packet to all players tracking the given entity
	/// @param entity The entity that's being tracked
	/// @param payload The main packet payload
	/// @param payloads Other packet payloads that can be sent with the main one
	/// @throws IllegalStateException If called on client
	public static void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload, CustomPacketPayload... payloads) {
		if (entity.level().isClientSide()) {
			throw new IllegalStateException("Cannot send clientbound payloads on the client");
		} else if (entity.level().getChunkSource() instanceof ServerChunkCache chunkCache) {
			chunkCache.sendToTrackingPlayers(entity, makeClientboundPacket(payload, payloads));
		}
	}

	/// Sends a packet to all player the given entity and the entity itself
	/// @param entity The entity that's being tracked
	/// @param payload The main packet payload
	/// @param payloads Other packet payloads that can be sent with the main one
	/// @throws IllegalStateException If called on client
	public static void sendToPlayersTrackingEntityAndSelf(Entity entity, CustomPacketPayload payload, CustomPacketPayload... payloads) {
		if (entity.level().isClientSide()) {
			throw new IllegalStateException("Cannot send clientbound payloads on the client");
		} else if (entity.level().getChunkSource() instanceof ServerChunkCache chunkCache) {
			chunkCache.sendToTrackingPlayersAndSelf(entity, makeClientboundPacket(payload, payloads));
		}
	}

	/// Sends a packet to all player tracking a chunk
	/// @param level The level the chunk its in
	/// @param chunkPos The chunk target position
	/// @param payload The main packet payload
	/// @param payloads Other packet payloads that can be sent with the main one
	public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload payload, CustomPacketPayload... payloads) {
		Packet<?> packet = makeClientboundPacket(payload, payloads);
		for (ServerPlayer player : level.getChunkSource().chunkMap.getPlayers(chunkPos, false)) {
			player.connection.send(packet);
		}
	}

	/// Sends a block entity's update packet if it has one to all the players tracking the block entity.
	/// @param blockEntity The target block entity
	public static void sendBlockEntityUpdate(BlockEntity blockEntity) {
		if (blockEntity.getLevel() instanceof ServerLevel level) {
			var updatePacket = blockEntity.getUpdatePacket();
			if (updatePacket != null) {
				level.getChunkSource().chunkMap.getPlayers(ChunkPos.containing(blockEntity.getBlockPos()), false)
						.forEach(player -> player.connection.send(updatePacket));
			}
		}
	}

	private static Packet<? super ClientGamePacketListener> makeClientboundPacket(CustomPacketPayload payload, CustomPacketPayload... payloads) {
		Objects.requireNonNull(payload, "Cannot send null payload");
		if (payloads.length > 0) {
			final List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>();
			packets.add(new ClientboundCustomPayloadPacket(payload));
			for (CustomPacketPayload otherPayload : payloads) {
				Objects.requireNonNull(otherPayload, "Cannot send null payload");
				packets.add(new ClientboundCustomPayloadPacket(otherPayload));
			}
			return new ClientboundBundlePacket(packets);
		} else {
			return new ClientboundCustomPayloadPacket(payload);
		}
	}

	private PacketDistributor() {}
}
