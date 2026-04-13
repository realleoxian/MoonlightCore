package de.realleoxian.moonlightcore.api.runtime;

import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.command.CommandsRegistrar;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.registry.RegistryHelper;
import de.realleoxian.moonlightcore.api.registry.RegistryInformationRegistrar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface MoonlightCoreRuntime<C extends ModLoadingRuntimeContext> {
    void onRuntimeInitialized(Runnable action);

    void initializeMod(String modId, C context, Runnable initializer);

    void commands(CommandsRegistrar registrar);

    void registry(String namespace, Consumer<RegistryHelper> initializer);

    void registryInformation(String namespace, Consumer<RegistryInformationRegistrar> initializer);

    void addServerReloadListener(ResourceLocation name, PreparableReloadListener reloadListener);

    boolean isModLoaded(String modId);

    NetworkHelper getNetworkHelper();

    EnvSide getEnvironmentSide();

    boolean isDevelopmentWorkspace();

    Path getGameDirectory();

    Path getConfigDirectory();
}
