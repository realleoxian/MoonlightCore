package de.leoxian.moonlightcore.api.network;

@FunctionalInterface
public interface PacketDecoder<B, V> {

    V read(B b);

}
