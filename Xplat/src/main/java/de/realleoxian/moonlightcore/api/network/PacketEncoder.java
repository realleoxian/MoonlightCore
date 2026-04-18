package de.realleoxian.moonlightcore.api.network;

@FunctionalInterface
public interface PacketEncoder<B, V> {

    void write(V v, B b);

}

