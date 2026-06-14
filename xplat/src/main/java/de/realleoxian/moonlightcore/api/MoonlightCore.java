package de.realleoxian.moonlightcore.api;

import de.realleoxian.moonlightcore.api.internal.XplatAbstraction;
import de.realleoxian.moonlightcore.api.internal.XplatAbstractionFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class MoonlightCore {
    public static final XplatAbstraction<ModLoadContext> ABSTRACTION = create();

    public static void initializeMod(String modId, ModLoadContext loadContext, Consumer<ModContainer> initializer) {
        ABSTRACTION.initializeMod(modId, loadContext, initializer);
    }

    public static LevelResource createLevelResource(String id) {
        return ABSTRACTION.createLevelResource(id);
    }

    public static SoundType createSoundType(float volume, float pitch, Supplier<SoundEvent> breakSound, Supplier<SoundEvent> stepSound, Supplier<SoundEvent> placeSound, Supplier<SoundEvent> hitSound, Supplier<SoundEvent> fallSound) {
        return ABSTRACTION.createSoundType(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound);
    }

    public static boolean isModLoaded(String modId) {
        return ABSTRACTION.isModLoaded(modId);
    }

    public static boolean isProduction() {
        return ABSTRACTION.isProduction();
    }

    public static boolean isFabric() {
        return ABSTRACTION.isFabric();
    }

    public static boolean isNeoforge() {
        return ABSTRACTION.isNeoforge();
    }

    public static Path getConfigDirectory() {
        return ABSTRACTION.getConfigDirectory();
    }

    public static Path getGameDirectory() {
        return ABSTRACTION.getGameDirectory();
    }

    public static EnvironmentSide getEnvironmentSide() {
        return ABSTRACTION.getEnvironmentSide();
    }

    @Nullable
    public static MinecraftServer getCurrentSever() {
        return ABSTRACTION.getCurrentSever();
    }

    @SuppressWarnings("unchecked")
    private static XplatAbstraction<ModLoadContext> create() {
        var loader = ServiceLoader.load(XplatAbstractionFactory.class);
        var factory = loader.findFirst().orElseThrow();
        return (XplatAbstraction<ModLoadContext>) factory.make();
    }

    private MoonlightCore() {}
}
