package de.leoxian.moonlightcore.internal.core.network.clientbound;

import de.leoxian.moonlightcore.internal.core.MoonlightCore;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.dimension.DimensionType;

public record ClientboundCreateDimensionPacketPayload(Identifier id, DimensionType dimensionType) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ClientboundCreateDimensionPacketPayload> TYPE = new Type<>(MoonlightCore.id("clientbound_create_dynamic_dimension"));
	public static final StreamCodec<? super RegistryFriendlyByteBuf, ClientboundCreateDimensionPacketPayload> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC, ClientboundCreateDimensionPacketPayload::id,
			ByteBufCodecs.fromCodec(DimensionType.DIRECT_CODEC), ClientboundCreateDimensionPacketPayload::dimensionType,
			ClientboundCreateDimensionPacketPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
