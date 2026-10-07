package de.leoxian.moonlightcore.client.network;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import de.leoxian.moonlightcore.common.network.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class ClientPlayNetworking {
	/// Register the handler for the payload type
	/// @param type The payload type
	/// @param handler The handler
	public static <MSG extends CustomPacketPayload> void registerHandler(CustomPacketPayload.Type<MSG> type, Handler<MSG> handler) {
		XplatClientAbstraction.INSTANCE.get().registerPlayPacketPayloadHandler(type, handler);
	}

	/// Return if the packet payload can be sent to the server
	/// @param type The packet type
	/// @return Whether the packet can be sent to the server
	public static boolean canSend(CustomPacketPayload.Type<?> type) {
		return XplatClientAbstraction.INSTANCE.get().canSendPlayPayload(type);
	}

	/// Return if the packet payload can be sent to the server
	/// @param payload The packet type
	/// @return Whether the packet can be sent to the server
	public static boolean canSend(CustomPacketPayload payload) {
		return canSend(payload.type());
	}

	private ClientPlayNetworking() {}

	@FunctionalInterface
	public interface Handler<T extends CustomPacketPayload> {
		/// Handles an incoming packet
		/// @param packet The packet
		/// @param context The network context
		void handle(T packet, Context context);
	}

	@ApiStatus.NonExtendable
	public interface Context {
		/// Enqueue a task to the main thread
		/// @param task The task
		CompletableFuture<Void> enqueueWork(Runnable task);

		/// Enqueue a task that returns a value to the main thread
		/// @param task The task
		<T> CompletableFuture<T> enqueueWork(Supplier<T> task);

		/// @return The connection
		ClientPacketListener packetListener();

		/// @return The client instance
		Minecraft minecraft();

		/// @return The local client player
		LocalPlayer player();

		/// @return The packet sender
		PacketSender responseSender();
	}
}
