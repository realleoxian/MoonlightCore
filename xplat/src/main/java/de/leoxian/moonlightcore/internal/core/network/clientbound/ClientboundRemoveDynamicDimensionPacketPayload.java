package de.leoxian.moonlightcore.internal.core.network.clientbound;

import de.leoxian.moonlightcore.internal.core.MoonlightCore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientboundRemoveDynamicDimensionPacketPayload(Identifier id) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ClientboundRemoveDynamicDimensionPacketPayload> TYPE = new Type<>(MoonlightCore.id("clientbound_remove_dynamic_dimension"));
	public static final StreamCodec<? super FriendlyByteBuf, ClientboundRemoveDynamicDimensionPacketPayload> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC, ClientboundRemoveDynamicDimensionPacketPayload::id,
			ClientboundRemoveDynamicDimensionPacketPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
