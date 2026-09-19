package de.leoxian.moonlightcore.fabric.client.platform;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.brigadier.CommandDispatcher;
import de.leoxian.moonlightcore.client.color.BlockColorRegistrar;
import de.leoxian.moonlightcore.client.command.ClientCommandsContext;
import de.leoxian.moonlightcore.client.fluid.FluidRendererRegistrar;
import de.leoxian.moonlightcore.client.gui.GuiLayer;
import de.leoxian.moonlightcore.client.gui.GuiLayerRegistrar;
import de.leoxian.moonlightcore.client.keymapping.KeyMappingRegistrar;
import de.leoxian.moonlightcore.client.menu.MenuScreenFactory;
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
import de.leoxian.moonlightcore.client.render.ClientTooltipComponentRegistrar;
import de.leoxian.moonlightcore.client.render.EntityRendererRegistrar;
import de.leoxian.moonlightcore.client.render.RenderPipelineRegistrar;
import de.leoxian.moonlightcore.common.ClientModEntrypoint;
import de.leoxian.moonlightcore.fabric.client.color.FabricBlockColorRegistrar;
import de.leoxian.moonlightcore.fabric.client.command.FabricClientCommandsContext;
import de.leoxian.moonlightcore.fabric.client.event.ClientEventHooks;
import de.leoxian.moonlightcore.fabric.client.fluid.FabricFluidRendererRegistrar;
import de.leoxian.moonlightcore.fabric.client.gui.FabricGuiLayer;
import de.leoxian.moonlightcore.fabric.client.gui.FabricGuiLayerRegistrar;
import de.leoxian.moonlightcore.fabric.client.keymapping.FabricKeyMappingRegistrar;
import de.leoxian.moonlightcore.fabric.client.menu.FabricMenuScreenRegistrar;
import de.leoxian.moonlightcore.fabric.client.model.FabricModelLayerRegistrar;
import de.leoxian.moonlightcore.fabric.client.model.FabricRangeSelectItemModelPropertyRegistrar;
import de.leoxian.moonlightcore.fabric.client.model.FabricSelectItemModelPropertyRegistrar;
import de.leoxian.moonlightcore.fabric.client.network.FabricClientConfigurationNetworkingContext;
import de.leoxian.moonlightcore.fabric.client.network.FabricClientPlayNetworkingContext;
import de.leoxian.moonlightcore.fabric.client.pack.FabricClientResourceReloadListenerRegistrar;
import de.leoxian.moonlightcore.fabric.client.particle.FabricParticleProviderRegistrar;
import de.leoxian.moonlightcore.fabric.client.render.FabricBlockEntityRendererRegistrar;
import de.leoxian.moonlightcore.fabric.client.render.FabricClientTooltipComponentRegistrar;
import de.leoxian.moonlightcore.fabric.client.render.FabricEntityRendererRegistrar;
import de.leoxian.moonlightcore.fabric.client.render.FabricRenderPipelineRegistrar;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FabricClientAbstractionImpl implements XplatClientAbstraction {
    @Override
    public void initializeClientMod(String modId, ClientModEntrypoint entrypoint) {
        try {
            entrypoint.initializeClientMod();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to initialize client mod '" + modId + "'", e);
        }
    }

    @Override
    public void fluidRenderer(String namespace, Consumer<FluidRendererRegistrar> initializer) {
        initializer.accept(FabricFluidRendererRegistrar.INSTANCE);
    }

    @Override
    public void guiLayers(String namespace, Consumer<GuiLayerRegistrar> initializer) {
        initializer.accept(FabricGuiLayerRegistrar.INSTANCE);
    }

    @Override
    public void keyMappings(String namespace, Consumer<KeyMappingRegistrar> initializer) {
        initializer.accept(FabricKeyMappingRegistrar.INSTANCE);
    }

    @Override
    public void modelLayers(String namespace, Consumer<ModelLayerRegistrar> initializer) {
        initializer.accept(FabricModelLayerRegistrar.INSTANCE);
    }

    @Override
    public void blockEntityRenderers(String namespace, Consumer<BlockEntityRendererRegistrar> initializer) {
        initializer.accept(FabricBlockEntityRendererRegistrar.INSTANCE);
    }

    @Override
    public void clientTooltips(String namespace, Consumer<ClientTooltipComponentRegistrar> initializer) {
        initializer.accept(FabricClientTooltipComponentRegistrar.INSTANCE);
    }

    @Override
    public void entityRenderers(String namespace, Consumer<EntityRendererRegistrar> initializer) {
        initializer.accept(FabricEntityRendererRegistrar.INSTANCE);
    }

    @Override
    public void particles(String namespace, Consumer<ParticleProviderRegistrar> initializer) {
        initializer.accept(FabricParticleProviderRegistrar.INSTANCE);
    }

    @Override
    public void renderPipelines(String namespace, Consumer<RenderPipelineRegistrar> initializer) {
        initializer.accept(FabricRenderPipelineRegistrar.INSTANCE);
    }

    @Override
    public void blockColor(String namespace, Consumer<BlockColorRegistrar> initializer) {
        initializer.accept(FabricBlockColorRegistrar.INSTANCE);
    }

    @Override
    public void menuScreens(String namespace, Consumer<MenuScreenRegistrar> initializer) {
        initializer.accept(FabricMenuScreenRegistrar.INSTANCE);
    }

    @Override
    public void resourceReloadListeners(String namespace, Consumer<ClientResourceReloadListenerRegistrar> initializer) {
        initializer.accept(FabricClientResourceReloadListenerRegistrar.INSTANCE);
    }

    @Override
    public void selectItemModelProperties(String namespace, Consumer<SelectItemModelPropertyRegistrar> initializer) {
        initializer.accept(FabricSelectItemModelPropertyRegistrar.INSTANCE);
    }

    @Override
    public void rangeSelectItemModelProperties(String namespace, Consumer<RangeSelectItemModelPropertyRegistrar> initializer) {
        initializer.accept(FabricRangeSelectItemModelPropertyRegistrar.INSTANCE);
    }

    @Override
    public void commands(Consumer<ClientCommandsContext> initializer) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
            initializer.accept(new FabricClientCommandsContext((CommandDispatcher<SharedSuggestionProvider>) (CommandDispatcher) dispatcher, buildContext));
        });
    }

    @Override
    public <MSG extends CustomPacketPayload> void registerPlayPayload(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> streamCodec, ClientPlayNetworking.Handler<MSG> handler) {
        PayloadTypeRegistry.clientboundPlay().register(type, streamCodec);
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            handler.handle(payload, new FabricClientPlayNetworkingContext(context));
        });
    }

    @Override
    public boolean canSendPlayPayload(CustomPacketPayload.Type<?> type) {
        return net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.canSend(type);
    }

    @Override
    public <T extends CustomPacketPayload> void registerConfigurationPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> streamCodec, ClientConfigurationNetworking.Handler<T> handler) {
        PayloadTypeRegistry.clientboundConfiguration().register(type, streamCodec);
        net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.registerGlobalReceiver(type, (payload, context) -> {
           handler.handle(payload, new FabricClientConfigurationNetworkingContext(context));
        });
    }

    @Override
    public boolean canSendConfigurationPayload(CustomPacketPayload.Type<?> type) {
        return net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.canSend(type);
    }

    @Override
    public void initialize() {
        ClientEventHooks.bindFabricApiEvents();
    }
}
