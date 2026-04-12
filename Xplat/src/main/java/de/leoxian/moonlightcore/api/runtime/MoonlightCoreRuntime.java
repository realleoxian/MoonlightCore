package de.leoxian.moonlightcore.api.runtime;

import de.leoxian.moonlightcore.api.EnvSide;
import de.leoxian.moonlightcore.api.command.CommandsRegistrar;
import de.leoxian.moonlightcore.api.network.NetworkHelper;
import de.leoxian.moonlightcore.api.registry.RegistryInformationRegistrar;
import de.leoxian.moonlightcore.api.registry.RegistryManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface MoonlightCoreRuntime<C extends ModLoadingRuntimeContext> {
    void initializeMod(String modId, C context, Runnable initializer);

    void commands(String namespace, CommandsRegistrar registrar);

    void registryInformation(String namespace, Consumer<RegistryInformationRegistrar> initializer);

    void addServerReloadListener(ResourceLocation name, PreparableReloadListener reloadListener);

    boolean isModLoaded(String modId);

    NetworkHelper getNetworkHelper();

    RegistryManager getRegistryManager();

    EnvSide getEnvironmentSide();

    boolean isDevelopmentWorkspace();

    Path getGameDirectory();

    Path getConfigDirectory();
}
