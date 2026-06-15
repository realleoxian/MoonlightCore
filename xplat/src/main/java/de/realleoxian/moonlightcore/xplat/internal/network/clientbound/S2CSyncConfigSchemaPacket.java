package de.realleoxian.moonlightcore.xplat.internal.network.clientbound;

import de.realleoxian.moonlightcore.xplat.config.sync.ConfigValueSyncChange;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record S2CSyncConfigSchemaPacket(ResourceLocation configName, List<ConfigValueSyncChange> changes) implements CustomPacketPayload {
    public static final Type<S2CSyncConfigSchemaPacket> TYPE = new Type<>(ResourceLocation.parse("moonlightcore:sync_config_schema"));
    public static final StreamCodec<FriendlyByteBuf, S2CSyncConfigSchemaPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public S2CSyncConfigSchemaPacket decode(FriendlyByteBuf byteBuf) {
            final var configName = byteBuf.readResourceLocation();
            final var changes = ConfigValueSyncChange.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(byteBuf);
            return new S2CSyncConfigSchemaPacket(configName, changes);
        }

        @Override
        public void encode(FriendlyByteBuf o, S2CSyncConfigSchemaPacket s2CSyncConfigSchemaPacket) {
            o.writeResourceLocation(s2CSyncConfigSchemaPacket.configName);
            o.writeVarInt(s2CSyncConfigSchemaPacket.changes().size());
            ConfigValueSyncChange.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(o, s2CSyncConfigSchemaPacket.changes);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
