package de.realleoxian.moonlightcore.api;

import de.realleoxian.moonlightcore.api.command.CommandRegistrar;
import de.realleoxian.moonlightcore.api.entity.EntityAttributeRegistrar;
import de.realleoxian.moonlightcore.api.runtime.XplatAbstraction;
import de.realleoxian.moonlightcore.api.runtime.XplatAbstractionFactory;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.ServiceLoader;
import java.util.function.Consumer;

public final class MoonlightCore {
    public static final XplatAbstraction<ModLoadContext> ABSTRACTION = create();

    public static void initializeMod(String modId, ModLoadContext loadContext, Consumer<ModContainer> initializer) {
        ABSTRACTION.initializeMod(modId, loadContext, initializer);
    }

    public static void entityAttributes(String namespace, Consumer<EntityAttributeRegistrar> initializer) {
        ABSTRACTION.entityAttributes(namespace, initializer);
    }

    public static void commands(String namespace, Consumer<CommandRegistrar> initializer) {
        ABSTRACTION.commands(namespace, initializer);
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
