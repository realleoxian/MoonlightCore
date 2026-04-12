package de.realleoxian.moonlightcore.api.client.runtime;

import de.realleoxian.moonlightcore.api.client.command.ClientCommandsRegistrar;
import de.realleoxian.moonlightcore.api.client.keymapping.KeyMappingRegistrar;
import de.realleoxian.moonlightcore.api.client.model.ModelLayerRegistrar;
import de.realleoxian.moonlightcore.api.client.model.plugin.ModelLoadPlugin;
import de.realleoxian.moonlightcore.api.client.particle.ParticleProviderRegistrar;
import de.realleoxian.moonlightcore.api.client.render.*;
import de.realleoxian.moonlightcore.api.client.render.color.BlockColorRegistrar;
import de.realleoxian.moonlightcore.api.client.render.color.ItemColorRegistrar;
import de.realleoxian.moonlightcore.api.runtime.ModLoadingRuntimeContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.Consumer;

public interface MoonlightCoreClientRuntime<C extends ModLoadingRuntimeContext> {
    void initializeClientMod(String modId, C context, Runnable initializer);

    void commands(String namespace, ClientCommandsRegistrar initializer);

    void modelLayers(String namespace, Consumer<ModelLayerRegistrar> registrar);

    void particles(String namespace, Consumer<ParticleProviderRegistrar> registrar);

    void entityRenderers(String namespace, Consumer<EntityRendererRegistrar> registrar);

    void blockEntityRenderers(String namespace, Consumer<BlockEntityRendererRegistrar> registrar);

    void blockColors(String namespace, Consumer<BlockColorRegistrar> registrar);

    void itemColors(String namespace, Consumer<ItemColorRegistrar> registrar);

    void chunkRenderLayers(String namespace, Consumer<ChunkRenderLayerRegistrar> registrar);

    void keyMappings(String namespace, Consumer<KeyMappingRegistrar> registrar);

    void registerPreparableReloadListener(ResourceLocation name, PreparableReloadListener listener);

    void registerModelLoadPlugin(ResourceLocation name, ModelLoadPlugin plugin);
}
