package de.realleoxian.moonlightcore.api.network;

@FunctionalInterface
public interface PacketDecoder<B, V> {

    V read(B b);

}
