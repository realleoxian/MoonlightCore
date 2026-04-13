package de.realleoxian.moonlightcore.api;

import de.realleoxian.moonlightcore.api.command.CommandsRegistrar;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.registry.RegistryHelper;
import de.realleoxian.moonlightcore.api.registry.RegistryInformationRegistrar;
import de.realleoxian.moonlightcore.api.runtime.ModLoadingRuntimeContext;
import de.realleoxian.moonlightcore.api.runtime.MoonlightCoreRuntime;
import de.realleoxian.moonlightcore.api.runtime.MoonlightCoreRuntimeFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.function.Consumer;

public final class MoonlightCore {
    static final MoonlightCoreRuntime<ModLoadingRuntimeContext> RUNTIME = create();

    public static void onRuntimeInitialized(Runnable action) {
        RUNTIME.onRuntimeInitialized(action);
    }

    public static void initializeMod(String modId, ModLoadingRuntimeContext context, Runnable initializer) {
        RUNTIME.initializeMod(modId, context, initializer);
    }

    public static void commands(CommandsRegistrar registrar) {
        RUNTIME.commands(registrar);
    }

    public static void registry(String namespace, Consumer<RegistryHelper> initializer) {
        RUNTIME.registry(namespace, initializer);
    }

    public static void registryInformation(String namespace, Consumer<RegistryInformationRegistrar> initializer) {
        RUNTIME.registryInformation(namespace, initializer);
    }

    public static void addServerReloadListener(ResourceLocation name, PreparableReloadListener reloadListener) {
        RUNTIME.addServerReloadListener(name, reloadListener);
    }

    public static boolean isModLoaded(String modId) {
        return RUNTIME.isModLoaded(modId);
    }

    public static NetworkHelper getNetworkHelper() {
        return RUNTIME.getNetworkHelper();
    }

    public static boolean isDevelopmentWorkspace() {
        return RUNTIME.isDevelopmentWorkspace();
    }

    public static Path getGameDirectory() {
        return RUNTIME.getGameDirectory();
    }

    public static Path getConfigDirectory() {
        return RUNTIME.getConfigDirectory();
    }

    public static MoonlightCoreRuntime<ModLoadingRuntimeContext> getRuntime() {
        return RUNTIME;
    }

    @SuppressWarnings("unchecked")
    private static MoonlightCoreRuntime<ModLoadingRuntimeContext> create() {
        var loader = ServiceLoader.load(MoonlightCoreRuntimeFactory.class);
        var factory = loader.findFirst().orElseThrow();
        return (MoonlightCoreRuntime<ModLoadingRuntimeContext>) factory.make();
    }

    private MoonlightCore() {}
}
