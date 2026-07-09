package de.leoxian.moonlightcore.common.config.schema.type;

import net.minecraft.IdentifierException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

import java.util.List;

public enum IdentifierConfigValueType implements ConfigValueType<Identifier> {
    INSTANCE
    ;

    @Override
    public String writeToString(Identifier value) {
        return value.toString();
    }

    @Override
    public DeserializationResult<Identifier> readFromString(String string) {
        try {
            string = string.trim();
            return new DeserializationResult.Success<>(Identifier.parse(string));
        } catch (IdentifierException e) {
            return new DeserializationResult.Error<>(List.of(e.getMessage()));
        }
    }

    @Override
    public void encodeToBuf(FriendlyByteBuf byteBuf, Identifier value) {
        byteBuf.writeIdentifier(value);
    }

    @Override
    public Identifier decodeFromBuf(FriendlyByteBuf byteBuf) {
        return byteBuf.readIdentifier();
    }
}
