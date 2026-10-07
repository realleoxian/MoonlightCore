package de.leoxian.moonlightcore.neoforge.client.platform;

import de.leoxian.moonlightcore.client.color.BlockColorRegistrar;
import de.leoxian.moonlightcore.client.command.ClientCommandsContext;
import de.leoxian.moonlightcore.client.fluid.FluidRendererRegistrar;
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
import de.leoxian.moonlightcore.client.render.ClientTooltipComponentRegistrar;
import de.leoxian.moonlightcore.client.render.EntityRendererRegistrar;
import de.leoxian.moonlightcore.client.render.RenderPipelineRegistrar;
import de.leoxian.moonlightcore.neoforge.client.color.NeoforgeBlockColorRegistrar;
import de.leoxian.moonlightcore.neoforge.client.command.NeoforgeClientCommandsContext;
import de.leoxian.moonlightcore.neoforge.client.fluid.NeoforgeFluidRendererRegistrar;
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
import de.leoxian.moonlightcore.neoforge.client.render.NeoforgeClientTooltipComponentRegistrar;
import de.leoxian.moonlightcore.neoforge.client.render.NeoforgeEntityRendererRegistrar;
import de.leoxian.moonlightcore.neoforge.client.render.NeoforgeRenderPipelineRegistrar;
import de.leoxian.moonlightcore.neoforge.common.hooks.EventBusesHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Consumer;

public class NeoforgeClientAbstraction implements XplatClientAbstraction {
	@Override
	public void fluidRenderer(String namespace, Consumer<FluidRendererRegistrar> initializer) {
		EventBusesHooks.atListener(namespace, NeoforgeFluidRendererRegistrar.class, initializer);
	}

	@Override
	public void guiLayers(String namespace, Consumer<GuiLayerRegistrar> initializer) {
		EventBusesHooks.whenAvailable(namespace, eventBus -> eventBus.addListener((RegisterGuiLayersEvent event) -> {
			initializer.accept(new NeoforgeGuiLayerRegistrar(event));
		}));
	}

	@Override
	public void keyMappings(String namespace, Consumer<KeyMappingRegistrar> initializer) {
		EventBusesHooks.whenAvailable(namespace, eventBus -> eventBus.addListener((RegisterKeyMappingsEvent event) -> {
			initializer.accept(new NeoforgeKeyMappingRegistrar(event));
		}));
	}

	@Override
	public void modelLayers(String namespace, Consumer<ModelLayerRegistrar> initializer) {
		EventBusesHooks.whenAvailable(namespace, eventBus -> eventBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) -> {
			initializer.accept(new NeoforgeModelLayerRegistrar(event));
		}));
	}

	@Override
	public void blockEntityRenderers(String namespace, Consumer<BlockEntityRendererRegistrar> initializer) {
		EventBusesHooks.whenAvailable(namespace, eventBus -> eventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
			initializer.accept(new NeoforgeBlockEntityRendererRegistrar(event));
		}));
	}

	@Override
	public void clientTooltips(String namespace, Consumer<ClientTooltipComponentRegistrar> initializer) {
		EventBusesHooks.whenAvailable(namespace, eventBus ->  eventBus.addListener((RegisterClientTooltipComponentFactoriesEvent event) -> {
			initializer.accept(new NeoforgeClientTooltipComponentRegistrar(event));
		}));
	}

	@Override
	public void entityRenderers(String namespace, Consumer<EntityRendererRegistrar> initializer) {
		EventBusesHooks.whenAvailable(namespace, eventBus -> eventBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
			initializer.accept(new NeoforgeEntityRendererRegistrar(event));
		}));
	}

	@Override
	public void particles(String namespace, Consumer<ParticleProviderRegistrar> initializer) {
		EventBusesHooks.whenAvailable(namespace, eventBus -> eventBus.addListener((RegisterParticleProvidersEvent event) -> {
			initializer.accept(new NeoforgeParticleProviderRegistrar(event));
		}));
	}

	@Override
	public void renderPipelines(String namespace, Consumer<RenderPipelineRegistrar> initializer) {
		EventBusesHooks.whenAvailable(namespace, eventBus -> eventBus.addListener((RegisterRenderPipelinesEvent event) -> {
			initializer.accept(new NeoforgeRenderPipelineRegistrar(event));
		}));
	}

	@Override
	public void blockColor(String namespace, Consumer<BlockColorRegistrar> initializer) {
		EventBusesHooks.whenAvailable(namespace, eventBus -> eventBus.addListener((RegisterColorHandlersEvent.BlockTintSources event) -> {
			initializer.accept(new NeoforgeBlockColorRegistrar(event));
		}));
	}

	@Override
	public void menuScreens(String namespace, Consumer<MenuScreenRegistrar> initializer) {
		EventBusesHooks.whenAvailable(namespace, eventBus -> eventBus.addListener((RegisterMenuScreensEvent event) -> {
			initializer.accept(new NeoforgeMenuScreenRegistrar(event));
		}));
	}

	@Override
	public void resourceReloadListeners(String namespace, Consumer<ClientResourceReloadListenerRegistrar> initializer) {
		EventBusesHooks.whenAvailable(namespace, eventBus -> eventBus.addListener((AddClientReloadListenersEvent event) -> {
			initializer.accept(new NeoforgeClientResourceReloadListenerRegistrar(event));
		}));
	}

	@Override
	public void selectItemModelProperties(String namespace, Consumer<SelectItemModelPropertyRegistrar> initializer) {
		EventBusesHooks.whenAvailable(namespace, eventBus -> eventBus.addListener((RegisterSelectItemModelPropertyEvent event) -> {
			initializer.accept(new NeoforgeSelectItemModelPropertyRegistrar(event));
		}));
	}

	@Override
	public void rangeSelectItemModelProperties(String namespace, Consumer<RangeSelectItemModelPropertyRegistrar> initializer) {
		EventBusesHooks.whenAvailable(namespace, eventBus -> eventBus.addListener((RegisterRangeSelectItemModelPropertyEvent event) -> {
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
	public <MSG extends CustomPacketPayload> void registerPlayPacketPayloadHandler(CustomPacketPayload.Type<MSG> type, ClientPlayNetworking.Handler<MSG> handler) {
		EventBusesHooks.getListener(type.id().getNamespace(), NeoforgeClientNetworkHandler.class)
						.playHandler(type, handler);
	}

	@Override
	public <MSG extends CustomPacketPayload> void registerConfigurationPacketPayloadHandler(CustomPacketPayload.Type<MSG> type, ClientConfigurationNetworking.Handler<MSG> handler) {
		EventBusesHooks.getListener(type.id().getNamespace(), NeoforgeClientNetworkHandler.class)
				.configurationHandler(type, handler);
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
