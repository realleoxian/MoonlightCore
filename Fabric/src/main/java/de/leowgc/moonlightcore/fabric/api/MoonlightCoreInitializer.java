package de.leowgc.moonlightcore.fabric.api;

public interface MoonlightCoreInitializer {

    default void onServerInitialized() {}

    void onInitialize();

    default void onClientInitialized() {}
}
