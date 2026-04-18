package de.realleoxian.moonlightcore.fabric.runtime;

import de.realleoxian.moonlightcore.api.EnvSide;
import de.realleoxian.moonlightcore.api.command.CommandsRegistrar;
import de.realleoxian.moonlightcore.api.misc.ModProxy;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.registry.RegistryHelper;
import de.realleoxian.moonlightcore.api.registry.RegistryInformationRegistrar;
import de.realleoxian.moonlightcore.api.server.permission.PermissionHelper;
import de.realleoxian.moonlightcore.fabric.network.FabricNetworkHelperImpl;
import de.realleoxian.moonlightcore.fabric.registry.FabricRegistryHelperImpl;
import de.realleoxian.moonlightcore.impl.runtime.XplatMoonlightCoreRuntime;
import de.realleoxian.moonlightcore.impl.server.permission.XplatPermissionHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class FabricMoonlightCoreRuntime extends XplatMoonlightCoreRuntime<EmptyModLoadingRuntimeContext> {
    private final List<CommandsRegistrar> commandsRegistrars = new ArrayList<>();

    private final NetworkHelper networkHelper = new FabricNetworkHelperImpl();

    private final ModProxy<PermissionHelper> permissionHelper = ModProxy.<PermissionHelper>of(XplatPermissionHelper::new)
                    .with("fabric-permissions-api-v0", "de.realleoxian.moonlightcore.fabric.compat.permission.FabricPermissionAPICompat");

    FabricMoonlightCoreRuntime() {
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> {
            this.commandsRegistrars.forEach((registrar) -> registrar.registerCommands(dispatcher, buildContext, selection));
        });
    }

    @Override
    public void initializeMod(String modId, EmptyModLoadingRuntimeContext context, Runnable initializer) {
        initializer.run();
    }

    @Override
    public void commands(CommandsRegistrar registrar) {
        this.commandsRegistrars.add(registrar);
    }

    @Override
    public void registry(String namespace, Consumer<RegistryHelper> initializer) {
        initializer.accept(new FabricRegistryHelperImpl(namespace));
    }

    @Override
    public void registryInformation(String namespace, Consumer<RegistryInformationRegistrar> initializer) {
        initializer.accept(information -> {
            boolean defaulted = information.defaultKey() != null;

            FabricRegistryBuilder<?, ?> builder = defaulted ?
                    FabricRegistryBuilder.createDefaulted(information.name(), information.defaultKey()) :
                    FabricRegistryBuilder.createSimple(information.name());

            if (information.isSync()) {
                builder = builder.attribute(RegistryAttribute.SYNCED);
            }
            builder.buildAndRegister();
        });
    }

    @Override
    public void addServerReloadListener(ResourceLocation name, PreparableReloadListener reloadListener) {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return name;
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return reloadListener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }
        });
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public NetworkHelper getNetworkHelper() {
        return this.networkHelper;
    }

    @Override
    public PermissionHelper getPermissionHelper() {
        return this.permissionHelper.build();
    }

    @Override
    public EnvSide getEnvironmentSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? EnvSide.CLIENT : EnvSide.SERVER;
    }

    @Override
    public boolean isDevelopmentWorkspace() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getGameDirectory() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
