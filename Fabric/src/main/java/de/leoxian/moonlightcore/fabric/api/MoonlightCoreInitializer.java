package de.leoxian.moonlightcore.fabric.api;

public interface MoonlightCoreInitializer {

     default void onClientInitialize() {}

     default void onModInitialize() {}

}
