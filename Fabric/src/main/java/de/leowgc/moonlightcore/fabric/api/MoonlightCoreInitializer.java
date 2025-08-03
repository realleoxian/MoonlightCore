package de.leowgc.moonlightcore.fabric.api;

public interface MoonlightCoreInitializer {

    void onInitialize();

    default void onClientInitialized() {}
}
