package de.leoxian.moonlightcore.common.network;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class ServerConfigurationNetworking {
	/// Registers a serverbound packet payload
	/// @param type The packet payload type
	/// @param codec The packet payload codec
	/// @param handler The handler used when the packet its received
	public static  <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> codec, ServerConfigurationNetworking.Handler<T> handler) {
		XplatAbstraction.INSTANCE.registerConfigurationPayload(type, codec, handler);
	}

	/// Checks if the given packet listener connection supports a payload
	/// @param packetListener The connection
	/// @param type The packet payload type
	/// @return Whether it can be sent or not
	public static boolean canSend(ServerConfigurationPacketListenerImpl packetListener, CustomPacketPayload.Type<?> type) {
		return XplatAbstraction.INSTANCE.canSendConfigurationPayload(packetListener, type);
	}

	/// Checks if the given packet listener connection supports a payload
	/// @param packetListener The connection
	/// @param payload The packet payload
	/// @return Whether it can be sent or not
	public static boolean canSend(ServerConfigurationPacketListenerImpl packetListener, CustomPacketPayload payload) {
		return canSend(packetListener, payload.type());
	}

	/// Adds a configuration task that can be executed at configuration phase when a player is joining
	/// @param modId The mod that it's adding the task
	/// @param packetListener The connection
	/// @param task The configuration task
	public static void addTask(String modId, ServerConfigurationPacketListenerImpl packetListener, ConfigurationTask task) {
		XplatAbstraction.INSTANCE.addConfigurationTask(modId, packetListener, task);
	}

	/// Completes a task that was being executed when a player was joining. Call this method everytime you add a new task.
	/// @param packetListener The connection
	/// @param type  The configuration task's type
	public static void completeTask(ServerConfigurationPacketListenerImpl packetListener, ConfigurationTask.Type type) {
		XplatAbstraction.INSTANCE.completeCurrentConfigurationTask(packetListener, type);
	}

	private ServerConfigurationNetworking() {}

	@FunctionalInterface
	public interface Handler<T extends CustomPacketPayload> {
		/// Handles an incoming packet
		/// @param packet The packet instance
		/// @param context The network context
		void handle(T packet, Context context);
	}

	@ApiStatus.NonExtendable
	public interface Context {
		/// Enqueues a task to be executed on the main thread
		/// @param task The task
		CompletableFuture<Void> enqueueWork(Runnable task);

		/// Enqueues a task that returns a value to be executed on the main thread
		/// @param task The task
		<T> CompletableFuture<T> enqueueWork(Supplier<T> task);

		/// @return The player's connection
		ServerConfigurationPacketListenerImpl packetListener();

		/// @return The packet sender
		PacketSender responseSender();
	}
}
