package de.realleoxian.moonlightcore.api.client;

import de.realleoxian.moonlightcore.api.client.command.ClientCommandsRegistrar;
import de.realleoxian.moonlightcore.api.client.keymapping.KeyMappingRegistrar;
import de.realleoxian.moonlightcore.api.client.model.ModelLayerRegistrar;
import de.realleoxian.moonlightcore.api.client.model.plugin.ModelLoadPlugin;
import de.realleoxian.moonlightcore.api.client.particle.ParticleProviderRegistrar;
import de.realleoxian.moonlightcore.api.client.render.BlockEntityRendererRegistrar;
import de.realleoxian.moonlightcore.api.client.render.ChunkRenderLayerRegistrar;
import de.realleoxian.moonlightcore.api.client.render.EntityRendererRegistrar;
import de.realleoxian.moonlightcore.api.client.render.color.BlockColorRegistrar;
import de.realleoxian.moonlightcore.api.client.render.color.ItemColorRegistrar;
import de.realleoxian.moonlightcore.api.client.runtime.MoonlightCoreClientRuntime;
import de.realleoxian.moonlightcore.api.client.runtime.MoonlightCoreClientRuntimeFactory;
import de.realleoxian.moonlightcore.api.runtime.ModLoadingRuntimeContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.Consumer;

public final class MoonlightCoreClient {
    private static final MoonlightCoreClientRuntime<ModLoadingRuntimeContext> RUNTIME = MoonlightCoreClientRuntimeFactory.createFactory().make();

    public static void onClientRuntimeInitialized(Runnable action) {
        RUNTIME.onClientRuntimeInitialized(action);
    }

    public static void initializeClientMod(String modId, ModLoadingRuntimeContext context, Runnable initializer) {
        RUNTIME.initializeClientMod(modId, context, initializer);
    }

    public static void commands(String namespace, ClientCommandsRegistrar initializer) {
        RUNTIME.commands(namespace, initializer);
    }

    public static void modelLayers(String namespace, Consumer<ModelLayerRegistrar> registrar) {
        RUNTIME.modelLayers(namespace, registrar);
    }

    public static void particles(String namespace, Consumer<ParticleProviderRegistrar> registrar) {
        RUNTIME.particles(namespace, registrar);
    }

    public static void entityRenderers(String namespace, Consumer<EntityRendererRegistrar> registrar) {
        RUNTIME.entityRenderers(namespace, registrar);
    }

    public static void blockEntityRenderers(String namespace, Consumer<BlockEntityRendererRegistrar> registrar) {
        RUNTIME.blockEntityRenderers(namespace, registrar);
    }

    public static void blockColors(String namespace, Consumer<BlockColorRegistrar> registrar) {
        RUNTIME.blockColors(namespace, registrar);
    }

    public static void itemColors(String namespace, Consumer<ItemColorRegistrar> registrar) {
        RUNTIME.itemColors(namespace, registrar);
    }

    public static void chunkRenderLayers(String namespace, Consumer<ChunkRenderLayerRegistrar> registrar) {
        RUNTIME.chunkRenderLayers(namespace, registrar);
    }

    public static void keyMappings(String namespace, Consumer<KeyMappingRegistrar> registrar) {
        RUNTIME.keyMappings(namespace, registrar);
    }

    public static void registerPreparableReloadListener(ResourceLocation name, PreparableReloadListener listener) {
        RUNTIME.registerPreparableReloadListener(name, listener);
    }

    public static void registerModelLoadPlugin(ResourceLocation name, ModelLoadPlugin plugin) {
        RUNTIME.registerModelLoadPlugin(name, plugin);
    }

    private MoonlightCoreClient() {}
}
