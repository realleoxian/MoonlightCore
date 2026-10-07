package de.leoxian.moonlightcore.common.network;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class ServerConfigurationNetworking {
	/// Register a packet payload handler
	/// @param type The packet payload the handler is for
	/// @param handler The handler
	public static <MSG extends CustomPacketPayload> void registerHandler(CustomPacketPayload.Type<MSG> type, ServerConfigurationNetworking.Handler<MSG> handler) {
		XplatAbstraction.INSTANCE.registerServerboundConfigurationPayloadHandler(type, handler);
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

		/// Complete the current configuration task if it's the given type
		/// @param type The task's type
		void completeTask(final ConfigurationTask.Type type);

		/// @return The player's connection
		ServerConfigurationPacketListenerImpl packetListener();

		/// @return The packet sender
		PacketSender responseSender();
	}
}
