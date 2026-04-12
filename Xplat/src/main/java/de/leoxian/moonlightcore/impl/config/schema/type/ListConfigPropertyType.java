package de.leoxian.moonlightcore.impl.config.schema.type;

import de.leoxian.moonlightcore.api.config.schema.ConfigPropertyType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListConfigPropertyType<E> implements ConfigPropertyType<List<E>> {
    private final ConfigPropertyType<E> elementType;
    private final int maxSize;

    public ListConfigPropertyType(ConfigPropertyType<E> elementType, int maxSize) {
        this.maxSize = maxSize;
        this.elementType = elementType;
    }

    @Override
    public String write(List<E> value) {
        return value.stream().map(this.elementType::write).collect(Collectors.joining(",", "[", "]"));
    }

    @Override
    public List<E> read(String str) {
        str = str.trim();
        if (str.startsWith("[")) {
            if (!str.endsWith("]")) {
                throw new IllegalArgumentException("Collections must start with an open bracket ('[') and a closing bracket (']') or having neither");
            }

            str = str.substring(1, str.length() - 1);
        } else if (str.endsWith("]")) {
            throw new IllegalArgumentException("Collections must start with an open bracket ('[') and a closing bracket (']') or having neither");
        }

        if (str.isEmpty()) return List.of();

        String[] split = str.split(",");
        int length = split.length;

        if (length > this.maxSize)
            throw new IllegalArgumentException("Exceeded the maximum amount of elements (%d > %d)".formatted(length, this.maxSize));

        List<E> collection = new ArrayList<>(length);
        Arrays.stream(split)
                .filter(s -> !s.isEmpty())
                .map(this.elementType::read)
                .forEach(collection::add);
        return collection;
    }

    @Override
    public void encodeToBuf(FriendlyByteBuf byteBuf, List<E> value) {
        byteBuf.writeVarInt(value.size());

        for (E element : value) {
            this.elementType.encodeToBuf(byteBuf, element);
        }
    }

    @Override
    public List<E> decodeFromBuf(FriendlyByteBuf byteBuf) {
        int size = byteBuf.readVarInt();

        List<E> elements = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            elements.add(this.elementType.decodeFromBuf(byteBuf));
        }
        return elements;
    }
}
