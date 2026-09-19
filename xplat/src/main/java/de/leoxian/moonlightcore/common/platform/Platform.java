package de.leoxian.moonlightcore.common.platform;

import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

import java.nio.file.Path;
import java.util.function.Supplier;

public final class Platform {
    /// Creates a new [SoundType]
    /// @param volume The volume
    /// @param pitch The pitch
    /// @param breakSound The sound used when breaking
    /// @param stepSound The sound used when stepping
    /// @param placeSound The sound used when placing
    /// @param hitSound The sound used when hitting
    /// @param fallSound The sound used when falling
    public static SoundType createSoundType(float volume, float pitch, Supplier<SoundEvent> breakSound, Supplier<SoundEvent> stepSound, Supplier<SoundEvent> placeSound, Supplier<SoundEvent> hitSound, Supplier<SoundEvent> fallSound) {
        return XplatAbstraction.INSTANCE.createSoundType(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound);
    }

    /// Check if a mod its present/loaded
    /// @param modId The expected mod id to be present
    /// @return Whether a mod its present or not
    public static boolean isModLoaded(String modId) {
        return XplatAbstraction.INSTANCE.isModLoaded(modId);
    }

    /// @return The current server
    public static MinecraftServer getCurrentServer() {
        return XplatAbstraction.INSTANCE.getCurrentServer();
    }

    /// @return The directory where are all configurations
    public static Path getConfigDirectory() {
        return XplatAbstraction.INSTANCE.getConfigDirectory();
    }

    /// @return The game's directory
    public static Path getGameDirectory() {
        return XplatAbstraction.INSTANCE.getGameDirectory();
    }

    /// @return Whether it's running on a development workspace
    public static boolean isDevelopmentWorkspace() {
        return XplatAbstraction.INSTANCE.isDevelopmentWorkspace();
    }

    /// @return Whether it's running on NeoForge
    public static boolean isNeoforge() {
        return XplatAbstraction.INSTANCE.isNeoforge();
    }

    /// @return Whether it's running on Fabric
    public static boolean isFabric() {
        return XplatAbstraction.INSTANCE.isFabric();
    }

    private Platform() {}
}
