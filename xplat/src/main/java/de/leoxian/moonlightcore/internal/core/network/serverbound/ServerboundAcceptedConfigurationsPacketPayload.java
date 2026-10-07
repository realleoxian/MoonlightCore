package de.leoxian.moonlightcore.internal.core.network.serverbound;

import de.leoxian.moonlightcore.internal.core.MoonlightCore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.Set;

public record ServerboundAcceptedConfigurationsPacketPayload(Set<Identifier> acceptedConfigurations) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ServerboundAcceptedConfigurationsPacketPayload> TYPE = new Type<>(MoonlightCore.id("serverbound_accepted_configurations"));
	public static final StreamCodec<? super FriendlyByteBuf, ServerboundAcceptedConfigurationsPacketPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.collection(HashSet::new, Identifier.STREAM_CODEC), ServerboundAcceptedConfigurationsPacketPayload::acceptedConfigurations,
			ServerboundAcceptedConfigurationsPacketPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
