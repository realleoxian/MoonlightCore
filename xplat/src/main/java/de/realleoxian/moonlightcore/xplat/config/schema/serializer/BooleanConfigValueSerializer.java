package de.realleoxian.moonlightcore.xplat.config.schema.serializer;

import de.realleoxian.moonlightcore.api.config.schema.ConfigValueSerializer;
import net.minecraft.network.FriendlyByteBuf;

public final class BooleanConfigValueSerializer implements ConfigValueSerializer<Boolean> {
    public static final ConfigValueSerializer<Boolean> INSTANCE = new BooleanConfigValueSerializer();

    @Override
    public DeserializationResult<Boolean> readFromString(String str) {
        str = str.trim();
        if ("true".equalsIgnoreCase(str)) return new DeserializationResult.Success<>(true);
        else if ("false".equalsIgnoreCase(str)) return new DeserializationResult.Success<>(false);

        return new DeserializationResult.Error<>("String must be 'true' or 'false'");
    }

    @Override
    public String writeToString(Boolean aBoolean) {
        return Boolean.toString(aBoolean);
    }

    @Override
    public void encodeToBuf(FriendlyByteBuf byteBuf, Boolean aBoolean) {
        byteBuf.writeBoolean(aBoolean);
    }

    @Override
    public Boolean decodeFromBuf(FriendlyByteBuf byteBuf) {
        return byteBuf.readBoolean();
    }

    private BooleanConfigValueSerializer() {}
}
