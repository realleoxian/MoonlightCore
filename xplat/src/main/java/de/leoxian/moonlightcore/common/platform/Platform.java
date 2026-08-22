package de.leoxian.moonlightcore.common.platform;

import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

import java.nio.file.Path;
import java.util.function.Supplier;

public final class Platform {
    public static SoundType createSoundType(float volume, float pitch, Supplier<SoundEvent> breakSound, Supplier<SoundEvent> stepSound, Supplier<SoundEvent> placeSound, Supplier<SoundEvent> hitSound, Supplier<SoundEvent> fallSound) {
        return XplatAbstraction.INSTANCE.createSoundType(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound);
    }

    public static boolean isModLoaded(String modId) {
        return XplatAbstraction.INSTANCE.isModLoaded(modId);
    }

    public static MinecraftServer getCurrentServer() {
        return XplatAbstraction.INSTANCE.getCurrentServer();
    }

    public static Path getConfigDirectory() {
        return XplatAbstraction.INSTANCE.getConfigDirectory();
    }

    public static Path getGameDirectory() {
        return XplatAbstraction.INSTANCE.getGameDirectory();
    }

    public static boolean isDevelopmentWorkspace() {
        return XplatAbstraction.INSTANCE.isDevelopmentWorkspace();
    }

    public static boolean isNeoforge() {
        return XplatAbstraction.INSTANCE.isNeoforge();
    }

    public static boolean isFabric() {
        return XplatAbstraction.INSTANCE.isFabric();
    }

    private Platform() {}
}
