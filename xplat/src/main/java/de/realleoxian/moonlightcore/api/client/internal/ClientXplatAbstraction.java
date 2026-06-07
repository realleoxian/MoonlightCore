package de.realleoxian.moonlightcore.api.client.internal;

import de.realleoxian.moonlightcore.api.ModLoadContext;
import de.realleoxian.moonlightcore.api.client.ClientModContainer;
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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface ClientXplatAbstraction<T extends ModLoadContext> {
    void initializeClientMod(String modId, T loadContext, Consumer<ClientModContainer> initializer);

    void shaders(String namespace, Consumer<ShaderRegistrar> initializer);

    void keyMappings(String namespace, Consumer<KeyMappingRegistrar> initializer);

    void particles(String namespace, Consumer<ParticleProviderRegistrar> initializer);

    void modelLayers(String namespace, Consumer<ModelLayerRegistrar> initializer);

    void chunkRenderLayers(String namespace, Consumer<ChunkRenderLayerRegistrar> initializer);

    void renderers(String namespace, Consumer<RendererRegistrar> initializer);

    void blockColors(String namespace, Consumer<BlockColorHandlerRegistrar> initializer);

    void itemColors(String namespace, Consumer<ItemColorHandlerRegistrar> initializer);

    void commands(String namespace, Consumer<ClientCommandsRegistrar> initializer);

    // -----[CLIENT NETWORKING]-----

    <MSG extends CustomPacketPayload> void registerConfigurationPayload(CustomPacketPayload.Type<MSG> type, StreamCodec<? super FriendlyByteBuf, MSG> codec, ClientNetworking.ConfigurationPayloadHandler<MSG> handler);

    <MSG extends CustomPacketPayload> void registerPlayPayload(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> codec, ClientNetworking.PlayPayloadHandler<MSG> handler);

    boolean canSendPlayPayload(CustomPacketPayload.Type<?> type);

    boolean canSendConfigurationPayload(CustomPacketPayload.Type<?> type);

    // -----[PLATFORM]-----

    void registerPreparableReloadListener(ResourceLocation name, PreparableReloadListener listener);
}
