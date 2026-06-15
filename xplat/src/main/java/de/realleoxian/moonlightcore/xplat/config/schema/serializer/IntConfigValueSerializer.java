package de.realleoxian.moonlightcore.xplat.config.schema.serializer;

import de.realleoxian.moonlightcore.api.config.schema.ConfigValueSerializer;
import net.minecraft.network.FriendlyByteBuf;

public final class IntConfigValueSerializer implements ConfigValueSerializer<Integer> {
    public static final ConfigValueSerializer<Integer> INSTANCE = new IntConfigValueSerializer();

    @Override
    public DeserializationResult<Integer> readFromString(String str) {
        str = str.trim();
        try {
            final int result = Integer.parseInt(str);
            return new DeserializationResult.Success<>(result);
        } catch (NumberFormatException e) {
            return new DeserializationResult.Error<>("Unable to parse int: '%s' with errors:\n%s".formatted(str, e.getMessage()));
        }
    }

    @Override
    public String writeToString(Integer integer) {
        return Integer.toString(integer);
    }

    @Override
    public void encodeToBuf(FriendlyByteBuf byteBuf, Integer integer) {
        byteBuf.writeInt(integer);
    }

    @Override
    public Integer decodeFromBuf(FriendlyByteBuf byteBuf) {
        return byteBuf.readInt();
    }

    private IntConfigValueSerializer() {}
}
