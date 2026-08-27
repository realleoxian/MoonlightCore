package de.leoxian.moonlightcore.neoforge.client.platform;

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
import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import de.leoxian.moonlightcore.client.render.BlockEntityRendererRegistrar;
import de.leoxian.moonlightcore.client.render.EntityRendererRegistrar;
import de.leoxian.moonlightcore.client.render.RenderPipelineRegistrar;
import de.leoxian.moonlightcore.common.ClientModEntrypoint;
import de.leoxian.moonlightcore.neoforge.client.color.NeoforgeBlockColorRegistrar;
import de.leoxian.moonlightcore.neoforge.client.command.NeoforgeClientCommandsContext;
import de.leoxian.moonlightcore.neoforge.client.gui.NeoforgeGuiLayerRegistrar;
import de.leoxian.moonlightcore.neoforge.client.keymapping.NeoforgeKeyMappingRegistrar;
import de.leoxian.moonlightcore.neoforge.client.menu.NeoforgeMenuScreenRegistrar;
import de.leoxian.moonlightcore.neoforge.client.model.NeoforgeModelLayerRegistrar;
import de.leoxian.moonlightcore.neoforge.client.model.NeoforgeRangeSelectItemModelPropertyRegistrar;
import de.leoxian.moonlightcore.neoforge.client.model.NeoforgeSelectItemModelPropertyRegistrar;
import de.leoxian.moonlightcore.neoforge.client.network.NeoforgeClientNetworkHandler;
import de.leoxian.moonlightcore.neoforge.client.pack.NeoforgeClientResourceReloadListenerRegistrar;
import de.leoxian.moonlightcore.neoforge.client.particle.NeoforgeParticleProviderRegistrar;
import de.leoxian.moonlightcore.neoforge.client.render.NeoforgeBlockEntityRendererRegistrar;
import de.leoxian.moonlightcore.neoforge.client.render.NeoforgeEntityRendererRegistrar;
import de.leoxian.moonlightcore.neoforge.client.render.NeoforgeRenderPipelineRegistrar;
import de.leoxian.moonlightcore.neoforge.common.ModEventBuses;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Consumer;

public class NeoforgeClientAbstraction implements XplatClientAbstraction {
    @Override
    public void initializeClientMod(String modId, ClientModEntrypoint entrypoint) {
        try {
            entrypoint.initializeClientMod();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to initialize client mod '" + modId + "'", e);
        }
    }

    @Override
    public void guiLayers(String namespace, Consumer<GuiLayerRegistrar> initializer) {
        ModEventBuses.getBus(namespace).ifPresent(eventBus -> eventBus.addListener((RegisterGuiLayersEvent event) -> {
            initializer.accept(new NeoforgeGuiLayerRegistrar(event));
        }));
    }

    @Override
    public void keyMappings(String namespace, Consumer<KeyMappingRegistrar> initializer) {
        ModEventBuses.getBus(namespace).ifPresent(eventBus -> eventBus.addListener((RegisterKeyMappingsEvent event) -> {
            initializer.accept(new NeoforgeKeyMappingRegistrar(event));
        }));
    }

    @Override
    public void modelLayers(String namespace, Consumer<ModelLayerRegistrar> initializer) {
        ModEventBuses.getBus(namespace).ifPresent(eventBus -> eventBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) -> {
            initializer.accept(new NeoforgeModelLayerRegistrar(event));
        }));
    }

    @Override
    public void blockEntityRenderers(String namespace, Consumer<BlockEntityRendererRegistrar> initializer) {
        ModEventBuses.getBus(namespace).ifPresent(eventBus -> eventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
            initializer.accept(new NeoforgeBlockEntityRendererRegistrar(event));
        }));
    }

    @Override
    public void entityRenderers(String namespace, Consumer<EntityRendererRegistrar> initializer) {
        ModEventBuses.getBus(namespace).ifPresent(eventBus -> eventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
            initializer.accept(new NeoforgeEntityRendererRegistrar(event));
        }));
    }

    @Override
    public void particles(String namespace, Consumer<ParticleProviderRegistrar> initializer) {
        ModEventBuses.getBus(namespace).ifPresent(eventBus -> eventBus.addListener((RegisterParticleProvidersEvent event) -> {
            initializer.accept(new NeoforgeParticleProviderRegistrar(event));
        }));
    }

    @Override
    public void renderPipelines(String namespace, Consumer<RenderPipelineRegistrar> initializer) {
        ModEventBuses.getBus(namespace).ifPresent(eventBus -> eventBus.addListener((RegisterRenderPipelinesEvent event) -> {
            initializer.accept(new NeoforgeRenderPipelineRegistrar(event));
        }));
    }

    @Override
    public void blockColor(String namespace, Consumer<BlockColorRegistrar> initializer) {
        ModEventBuses.getBus(namespace).ifPresent(eventBus -> eventBus.addListener((RegisterColorHandlersEvent.BlockTintSources event) -> {
            initializer.accept(new NeoforgeBlockColorRegistrar(event));
        }));
    }

    @Override
    public void menuScreens(String namespace, Consumer<MenuScreenRegistrar> initializer) {
        ModEventBuses.getBus(namespace).ifPresent(eventBus -> eventBus.addListener((RegisterMenuScreensEvent event) -> {
            initializer.accept(new NeoforgeMenuScreenRegistrar(event));
        }));
    }

    @Override
    public void resourceReloadListeners(String namespace, Consumer<ClientResourceReloadListenerRegistrar> initializer) {
        ModEventBuses.getBus(namespace).ifPresent(eventBus -> eventBus.addListener((AddClientReloadListenersEvent event) -> {
            initializer.accept(new NeoforgeClientResourceReloadListenerRegistrar(event));
        }));
    }

    @Override
    public void selectItemModelProperties(String namespace, Consumer<SelectItemModelPropertyRegistrar> initializer) {
        ModEventBuses.getBus(namespace).ifPresent(eventBus -> eventBus.addListener((RegisterSelectItemModelPropertyEvent event) -> {
           initializer.accept(new NeoforgeSelectItemModelPropertyRegistrar(event));
        }));
    }

    @Override
    public void rangeSelectItemModelProperties(String namespace, Consumer<RangeSelectItemModelPropertyRegistrar> initializer) {
        ModEventBuses.getBus(namespace).ifPresent(eventBus -> eventBus.addListener((RegisterRangeSelectItemModelPropertyEvent event) -> {
            initializer.accept(new NeoforgeRangeSelectItemModelPropertyRegistrar(event));
        }));
    }

    @Override
    public void commands(Consumer<ClientCommandsContext> initializer) {
        NeoForge.EVENT_BUS.addListener((RegisterClientCommandsEvent event) -> {
            initializer.accept(new NeoforgeClientCommandsContext(event));
        });
    }

    @Override
    public <MSG extends CustomPacketPayload> void registerPlayPayload(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> streamCodec, ClientPlayNetworking.Handler<MSG> handler) {
        ModEventBuses.registerListener(type.id().getNamespace(), NeoforgeClientNetworkHandler.class)
                .registerPlay(type, streamCodec, handler);
    }

    @Override
    public boolean canSendPlayPayload(CustomPacketPayload.Type<?> type) {
        ClientPacketListener packetListener = Minecraft.getInstance().getConnection();
        if (packetListener == null) {
            return false;
        }
        return packetListener.hasChannel(type);
    }

    @Override
    public <T extends CustomPacketPayload> void registerConfigurationPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> streamCodec, ClientConfigurationNetworking.Handler<T> handler) {
        ModEventBuses.registerListener(type.id().getNamespace(), NeoforgeClientNetworkHandler.class)
                .registerConfiguration(type, streamCodec, handler);
    }

    @Override
    public boolean canSendConfigurationPayload(CustomPacketPayload.Type<?> type) {
        ClientPacketListener packetListener = Minecraft.getInstance().getConnection();
        if (packetListener == null) {
            return false;
        }
        return packetListener.hasChannel(type);
    }

    @Override
    public void initialize() {

    }
}
