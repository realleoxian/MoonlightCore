package de.leoxian.moonlightcore.common.config.schema.type;

import net.minecraft.network.FriendlyByteBuf;

import java.util.List;
import java.util.UUID;

public enum UUIDConfigValueType implements ConfigValueType<UUID> {
    INSTANCE
    ;

    @Override
    public String writeToString(UUID value) {
        return value.toString();
    }

    @Override
    public DeserializationResult<UUID> readFromString(String string) {
        try {
            string = string.trim();
            return new DeserializationResult.Success<>(UUID.fromString(string));
        } catch (Exception e) {
            return new DeserializationResult.Error<>(List.of(e.getMessage()));
        }
    }

    @Override
    public void encodeToBuf(FriendlyByteBuf byteBuf, UUID value) {
        byteBuf.writeUUID(value);
    }

    @Override
    public UUID decodeFromBuf(FriendlyByteBuf byteBuf) {
        return byteBuf.readUUID();
    }
}
