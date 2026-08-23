package de.leoxian.moonlightcore.client.platform;

import de.leoxian.moonlightcore.client.color.BlockColorRegistrar;
import de.leoxian.moonlightcore.client.command.ClientCommandsContext;
import de.leoxian.moonlightcore.client.gui.GuiLayerRegistrar;
import de.leoxian.moonlightcore.client.keymapping.KeyMappingRegistrar;
import de.leoxian.moonlightcore.client.menu.MenuScreenRegistrar;
import de.leoxian.moonlightcore.client.model.ModelLayerRegistrar;
import de.leoxian.moonlightcore.client.model.RangeSelectItemModelPropertyRegistrar;
import de.leoxian.moonlightcore.client.model.SelectItemModelPropertyRegistrar;
import de.leoxian.moonlightcore.client.network.ClientConfigurationNetworking;
import de.leoxian.moonlightcore.client.network.ClientPlayNetworking;
import de.leoxian.moonlightcore.client.pack.ClientResourceReloadListenerRegistrar;
import de.leoxian.moonlightcore.client.particle.ParticleProviderRegistrar;
import de.leoxian.moonlightcore.client.render.BlockEntityRendererRegistrar;
import de.leoxian.moonlightcore.client.render.EntityRendererRegistrar;
import de.leoxian.moonlightcore.client.render.RenderPipelineRegistrar;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ServiceLoader;
import java.util.function.Consumer;

public interface XplatClientAbstraction {
    XplatClientAbstraction INSTANCE = ServiceLoader.load(XplatClientAbstractionFactory.class).findFirst().orElseThrow().create();

    void initializeClientMod(String modId, Class<?> initializer);

    // |-----| Registrars |-----|
    void guiLayers(String namespace, Consumer<GuiLayerRegistrar> initializer);

    void keyMappings(String namespace, Consumer<KeyMappingRegistrar> initializer);

    void modelLayers(String namespace, Consumer<ModelLayerRegistrar> initializer);

    void blockEntityRenderers(String namespace, Consumer<BlockEntityRendererRegistrar> initializer);

    void entityRenderers(String namespace, Consumer<EntityRendererRegistrar> initializer);

    void particles(String namespace, Consumer<ParticleProviderRegistrar> initializer);

    void renderPipelines(String namespace, Consumer<RenderPipelineRegistrar> initializer);

    void blockColor(String namespace, Consumer<BlockColorRegistrar> initializer);

    void menuScreens(String namespace, Consumer<MenuScreenRegistrar> initializer);

    void resourceReloadListeners(String namespace, Consumer<ClientResourceReloadListenerRegistrar> initializer);

    void selectItemModelProperties(String namespace, Consumer<SelectItemModelPropertyRegistrar> initializer);

    void rangeSelectItemModelProperties(String namespace, Consumer<RangeSelectItemModelPropertyRegistrar> initializer);

    void commands(Consumer<ClientCommandsContext> initializer);

    // |-----| C2S Play Networking |-----|

    <MSG extends CustomPacketPayload> void registerPlayPayload(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> streamCodec, ClientPlayNetworking.Handler<MSG> handler);

    boolean canSendPlayPayload(CustomPacketPayload.Type<?> type);

    // |-----| C2S Configuration Networking |-----|

    <T extends CustomPacketPayload> void registerConfigurationPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> streamCodec, ClientConfigurationNetworking.Handler<T> handler);

    boolean canSendConfigurationPayload(CustomPacketPayload.Type<?> type);

    void initialize();
}
