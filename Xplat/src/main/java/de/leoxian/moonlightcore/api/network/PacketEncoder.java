package de.leoxian.moonlightcore.api.network;

@FunctionalInterface
public interface PacketEncoder<B, V> {

    void write(B b, V v);

}

