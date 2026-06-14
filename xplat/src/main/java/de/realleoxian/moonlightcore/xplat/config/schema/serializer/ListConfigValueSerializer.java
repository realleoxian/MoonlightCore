package de.realleoxian.moonlightcore.xplat.config.schema.serializer;

import de.realleoxian.moonlightcore.api.config.schema.ConfigValueSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ListConfigValueSerializer<T> implements ConfigValueSerializer<List<T>> {
    private final ConfigValueSerializer<T> elementSerializer;

    public ListConfigValueSerializer(ConfigValueSerializer<T> elementSerializer) {
        this.elementSerializer = elementSerializer;
    }

    @Override
    public DeserializationResult<List<T>> readFromString(String str) {
        str = str.trim();
        if (str.startsWith("[")) {
            if (!str.endsWith("]")) {
               return new DeserializationResult.Error<>("No closing brace found. List must have no braces, or be wrapped in '[' and ']'");
            }

            str = str.substring(1, str.length() - 1);
        } else if (str.endsWith("]")) {
            return new DeserializationResult.Error<>("No opening brace found. List must have no braces, or be wrapped in '[' and ']'");
        }

        final var split = str.split(",");
        List<String> errors = new ArrayList<>();
        List<T> results = Arrays.stream(split)
                .map(String::trim).filter(s -> !s.isEmpty())
                .map(this.elementSerializer::readFromString)
                .<T>mapMulti((r, c) -> {
                    r.ifSuccess(c);
                    r.ifErrors(errors::add);
                }).toList();

        if (!errors.isEmpty()) {
            String errorMessage = errors.stream().collect(Collectors.joining("\n    - ", "Found errors on list:\n    - ", ""));
            return new DeserializationResult.Error<>(errorMessage);
        }
        return new DeserializationResult.Success<>(results);
    }

    @Override
    public String writeToString(List<T> ts) {
        return ts.stream().map(this.elementSerializer::writeToString).collect(Collectors.joining(", "));
    }
}
