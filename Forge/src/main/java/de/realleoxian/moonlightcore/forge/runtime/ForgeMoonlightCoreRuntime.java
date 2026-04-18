package de.realleoxian.moonlightcore.forge.runtime;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.command.CommandsRegistrar;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.registry.RegistryHelper;
import de.realleoxian.moonlightcore.api.registry.RegistryInformationRegistrar;
import de.realleoxian.moonlightcore.api.server.permission.PermissionHelper;
import de.realleoxian.moonlightcore.forge.network.ForgeNetworkHelper;
import de.realleoxian.moonlightcore.forge.platform.ModEventBusHandler;
import de.realleoxian.moonlightcore.forge.registry.ForgeRegistryInformationRegistrar;
import de.realleoxian.moonlightcore.forge.registry.ForgeRegistryManager;
import de.realleoxian.moonlightcore.forge.server.permission.ForgePermissionHelperImpl;
import de.realleoxian.moonlightcore.impl.runtime.XplatMoonlightCoreRuntime;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ForgeMoonlightCoreRuntime extends XplatMoonlightCoreRuntime<ForgeModLoadingContext> {
    private final List<CommandsRegistrar> commandsRegistrar = new ArrayList<>();
    private final List<Pair<ResourceLocation, PreparableReloadListener>> serverReloadListeners = new ArrayList<>();

    private final PermissionHelper permissionHelper = new ForgePermissionHelperImpl();
    private final NetworkHelper networkHelper = new ForgeNetworkHelper();

    ForgeMoonlightCoreRuntime() {
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
            CommandBuildContext buildContext = event.getBuildContext();
            Commands.CommandSelection selection = event.getCommandSelection();

            this.commandsRegistrar.forEach(registrar -> registrar.registerCommands(dispatcher, buildContext, selection));
        });

        MinecraftForge.EVENT_BUS.addListener((AddReloadListenerEvent event) -> this.serverReloadListeners.stream().map(Pair::getSecond).forEach(event::addListener));
    }

    @Override
    public void initializeMod(String modId, ForgeModLoadingContext context, Runnable initializer) {
        ModEventBusHandler.register(modId, context.eventBus());
        initializer.run();
    }

    @Override
    public void commands(CommandsRegistrar registrar) {
        this.commandsRegistrar.add(registrar);
    }

    @Override
    public void registryInformation(String namespace, Consumer<RegistryInformationRegistrar> initializer) {
        ForgeRegistryInformationRegistrar registrar = ModEventBusHandler.getRegistration(namespace, ForgeRegistryInformationRegistrar.class);
        initializer.accept(registrar);
    }

    @Override
    public void registry(String namespace, Consumer<RegistryHelper> initializer) {
        ForgeRegistryManager manager = ModEventBusHandler.getRegistration(namespace, ForgeRegistryManager.class);
        initializer.accept(manager);
    }

    @Override
    public void addServerReloadListener(ResourceLocation name, PreparableReloadListener reloadListener) {
        this.serverReloadListeners.add(Pair.of(name, reloadListener));
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public NetworkHelper getNetworkHelper() {
        return this.networkHelper;
    }

    @Override
    public PermissionHelper getPermissionHelper() {
        return this.permissionHelper;
    }

    @Override
    public EnvSide getEnvironmentSide() {
        return FMLLoader.getDist().isClient() ? EnvSide.CLIENT : EnvSide.SERVER;
    }

    @Override
    public boolean isDevelopmentWorkspace() {
        return FMLLoader.isProduction();
    }

    @Override
    public Path getGameDirectory() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
