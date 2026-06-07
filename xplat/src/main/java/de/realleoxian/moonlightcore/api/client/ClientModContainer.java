package de.realleoxian.moonlightcore.api.client;

import de.realleoxian.moonlightcore.api.client.command.ClientCommandsRegistrar;
import de.realleoxian.moonlightcore.api.client.keymapping.KeyMappingRegistrar;
import de.realleoxian.moonlightcore.api.client.model.ModelLayerRegistrar;
import de.realleoxian.moonlightcore.api.client.network.ClientNetworking;
import de.realleoxian.moonlightcore.api.client.particle.ParticleProviderRegistrar;
import de.realleoxian.moonlightcore.api.client.render.BlockColorHandlerRegistrar;
import de.realleoxian.moonlightcore.api.client.render.ChunkRenderLayerRegistrar;
import de.realleoxian.moonlightcore.api.client.render.ItemColorHandlerRegistrar;
import de.realleoxian.moonlightcore.api.client.render.RendererRegistrar;
import de.realleoxian.moonlightcore.api.client.shader.ShaderRegistrar;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface ClientModContainer {
    String namespace();

    default void shaders(Consumer<ShaderRegistrar> initializer) {
        MoonlightCoreClient.ABSTRACTION.shaders(namespace(), initializer);
    }

    default void keyMappings(Consumer<KeyMappingRegistrar> initializer) {
        MoonlightCoreClient.ABSTRACTION.keyMappings(namespace(), initializer);
    }

    default void particles(Consumer<ParticleProviderRegistrar> initializer) {
        MoonlightCoreClient.ABSTRACTION.particles(namespace(), initializer);
    }

    default void modelLayers(Consumer<ModelLayerRegistrar> initializer) {
        MoonlightCoreClient.ABSTRACTION.modelLayers(namespace(), initializer);
    }

    default void chunkRenderLayers(Consumer<ChunkRenderLayerRegistrar> initializer) {
        MoonlightCoreClient.ABSTRACTION.chunkRenderLayers(namespace(), initializer);
    }

    default void renderers(Consumer<RendererRegistrar> initializer) {
        MoonlightCoreClient.ABSTRACTION.renderers(namespace(), initializer);
    }

    default void blockColors(Consumer<BlockColorHandlerRegistrar> initializer) {
        MoonlightCoreClient.ABSTRACTION.blockColors(namespace(), initializer);
    }

    default void itemColors(Consumer<ItemColorHandlerRegistrar> initializer) {
        MoonlightCoreClient.ABSTRACTION.itemColors(namespace(), initializer);
    }

    default void commands(String namespace, Consumer<ClientCommandsRegistrar> initializer) {
        MoonlightCoreClient.ABSTRACTION.commands(namespace, initializer);
    }
}
