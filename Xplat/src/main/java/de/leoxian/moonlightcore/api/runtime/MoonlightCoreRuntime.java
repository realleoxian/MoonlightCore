package de.leoxian.moonlightcore.api.runtime;

import de.leoxian.moonlightcore.api.EnvSide;
import de.leoxian.moonlightcore.api.command.CommandsRegistrarContext;
import de.leoxian.moonlightcore.api.network.NetworkHelper;
import de.leoxian.moonlightcore.api.registry.RegistryCreatorInitializer;
import de.leoxian.moonlightcore.api.registry.RegistryManager;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface MoonlightCoreRuntime<C extends ModLoadingRuntimeContext> {

    MoonlightCoreRuntime<ModLoadingRuntimeContext> RUNTIME = MoonlightCoreRuntimeFactory.createFactory().make();

    void initializeMod(ResourceLocation name, C context, Runnable initializer);

    void commands(String namespace, Consumer<CommandsRegistrarContext> initializer);

    void registryCreator(String namespace, Consumer<RegistryCreatorInitializer> initializer);

    boolean isModLoaded(String modId);

    NetworkHelper getNetworkHelper();

    RegistryManager getRegistryManager();

    EnvSide getEnvironmentSide();

    boolean isDevelopmentWorkspace();

    Path getGameDirectory();

    Path getConfigDirectory();

}
