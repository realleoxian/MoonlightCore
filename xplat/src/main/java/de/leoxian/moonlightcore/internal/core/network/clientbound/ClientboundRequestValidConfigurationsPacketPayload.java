package de.leoxian.moonlightcore.internal.core.network.clientbound;

import de.leoxian.moonlightcore.internal.core.MoonlightCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public enum ClientboundRequestValidConfigurationsPacketPayload implements CustomPacketPayload {
	INSTANCE
	;
	public static final CustomPacketPayload.Type<ClientboundRequestValidConfigurationsPacketPayload> TYPE = new Type<>(MoonlightCore.id("clientbound_request_valid_configurations"));
	public static final StreamCodec<? super ByteBuf, ClientboundRequestValidConfigurationsPacketPayload> STREAM_CODEC = StreamCodec.unit(ClientboundRequestValidConfigurationsPacketPayload.INSTANCE);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
