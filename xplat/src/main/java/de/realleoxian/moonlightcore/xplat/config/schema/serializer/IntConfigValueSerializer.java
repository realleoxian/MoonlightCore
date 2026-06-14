package de.realleoxian.moonlightcore.xplat.config.schema.serializer;

import de.realleoxian.moonlightcore.api.config.schema.ConfigValueSerializer;

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

    private IntConfigValueSerializer() {}
}
