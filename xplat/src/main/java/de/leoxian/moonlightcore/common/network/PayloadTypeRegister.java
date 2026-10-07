package de.leoxian.moonlightcore.common.network;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class PayloadTypeRegister {
	/// Register a serverbound play packet payload type
	/// @param type The type
	/// @param streamCodec The codec
	/// @param handler The handler used when the packet its received
	public static <MSG extends CustomPacketPayload> void serverboundPlay(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> streamCodec, ServerPlayNetworking.Handler<MSG> handler) {
		XplatAbstraction.INSTANCE.registerServerboundPlayPacketPayload(type, streamCodec, handler);
	}

	/// Register a clientbound play packet payload type
	/// @param type The type
	/// @param streamCodec The codec
	public static <MSG extends CustomPacketPayload> void clientboundPlay(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> streamCodec) {
		XplatAbstraction.INSTANCE.registerClientboundPlayPayloadPacket(type, streamCodec);
	}

	/// Register a serverbound configuration packet payload type
	/// @param type The type
	/// @param streamCodec The codec
	/// @param handler The handler used when the packet its received
	public static <MSG extends CustomPacketPayload> void serverboundConfiguration(CustomPacketPayload.Type<MSG> type, StreamCodec<? super FriendlyByteBuf, MSG> streamCodec, ServerConfigurationNetworking.Handler<MSG> handler) {
		XplatAbstraction.INSTANCE.registerServerboundConfigurationPacketPayload(type, streamCodec, handler);
	}

	/// Register a clienbound configuration packet payload type
	/// @param type The type
	/// @param streamCodec The codec
	public static <MSG extends CustomPacketPayload> void clientboundConfiguration(CustomPacketPayload.Type<MSG> type, StreamCodec<? super FriendlyByteBuf, MSG> streamCodec) {
		XplatAbstraction.INSTANCE.registerClientboundConfigurationPayloadPacket(type, streamCodec);
	}

	private PayloadTypeRegister() {

	}
}
