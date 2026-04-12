package de.realleoxian.moonlightcore.api;

import de.realleoxian.moonlightcore.api.attachment.AttachmentType;
import de.realleoxian.moonlightcore.api.command.CommandsRegistrar;
import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.registry.RegistryInformationRegistrar;
import de.realleoxian.moonlightcore.api.registry.RegistryManager;
import de.realleoxian.moonlightcore.api.runtime.ModLoadingRuntimeContext;
import de.realleoxian.moonlightcore.api.runtime.MoonlightCoreRuntime;
import de.realleoxian.moonlightcore.api.runtime.MoonlightCoreRuntimeFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class MoonlightCore {
    static final MoonlightCoreRuntime<ModLoadingRuntimeContext> RUNTIME = MoonlightCoreRuntimeFactory.createFactory().make();

    public static final Supplier<Registry<AttachmentType<?>>> ATTACHMENT_TYPE_REGISTRY = RegistryManager.get().getRegistry(Registries.ATTACHMENT_TYPE);

    public static void initializeMod(String modId, ModLoadingRuntimeContext context, Runnable initializer) {
        RUNTIME.initializeMod(modId, context, initializer);
    }

    public static void commands(String namespace, CommandsRegistrar registrar) {
        RUNTIME.commands(namespace, registrar);
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

    public static RegistryManager getRegistryManager() {
        return RUNTIME.getRegistryManager();
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

    private MoonlightCore() {}

    public static final class Registries {
        public static final ResourceKey<Registry<AttachmentType<?>>> ATTACHMENT_TYPE = ResourceKey.createRegistryKey(new ResourceLocation("moonlightcore", "attachment_type"));

        private Registries() {}
    }
}
