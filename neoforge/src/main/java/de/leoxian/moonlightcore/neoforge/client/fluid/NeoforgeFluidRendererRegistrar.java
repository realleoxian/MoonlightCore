package de.leoxian.moonlightcore.neoforge.client.fluid;

import de.leoxian.moonlightcore.client.fluid.FluidRenderHandler;
import de.leoxian.moonlightcore.client.fluid.FluidRendererRegistrar;
import de.leoxian.moonlightcore.common.transfer.fluid.FluidResource;
import de.leoxian.moonlightcore.neoforge.common.ModEventBusRegistrable;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.event.entity.player.FluidTooltipEvent;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber
public class NeoforgeFluidRendererRegistrar implements FluidRendererRegistrar, ModEventBusRegistrable {
	private final Map<Holder<Fluid>, FluidModel.Unbaked> models = new HashMap<>();
	private static final Map<Holder<Fluid>, FluidRenderHandler> renderHandlers = new HashMap<>();

	@Override
	public void register(IEventBus modEventBus) {
		modEventBus.addListener((RegisterFluidModelsEvent event) -> this.models.forEach((fluidHolder, unbaked) -> event.register(unbaked, fluidHolder.value())));
		modEventBus.addListener((RegisterColorHandlersEvent.BlockTintSources event) -> {
			renderHandlers.forEach((fluidHolder, renderHandler) -> {
				Block block = fluidHolder.value().defaultFluidState().createLegacyBlock().getBlock();
				if (block != Blocks.AIR) {
					final Fluid targetFluid = fluidHolder.value();

					event.register(List.of(new FluidTintSource() {
						final FluidResource resource = FluidResource.of(targetFluid);

						@Override
						public int color(FluidState state) {
							return renderHandler.getColor(resource, null, null);
						}

						@Override
						public int colorAsTerrainParticle(BlockState state, BlockAndTintGetter level, BlockPos pos) {
							return renderHandler.getColor(resource, level, pos);
						}

						@Override
						public int colorInWorld(FluidState fluidState, BlockState blockState, BlockAndTintGetter level, BlockPos pos) {
							return renderHandler.getColor(resource, level, pos);
						}
					}));
				}
			});
		});
		modEventBus.addListener((RegisterClientExtensionsEvent event) -> {
			renderHandlers.forEach((fluidHolder, renderHandler) -> {
				FluidType type = fluidHolder.value().getFluidType();
				if (!event.isFluidTypeRegistered(type)) {
					event.registerFluidType(new NeoforgeFluidRenderHandler(fluidHolder, renderHandler), type);
				}
			});
		});
	}

	@SubscribeEvent
	public static void registerTooltips(FluidTooltipEvent event) {
		renderHandlers.forEach((fluidHolder, renderHandler) -> {
			FluidResource resource = FluidResource.of(fluidHolder.value());
			renderHandler.appendTooltip(resource, event.getToolTip(), event.getFlags());
		});
	}

	@Override
	public void registerModel(Holder<Fluid> holder, FluidModel.Unbaked model) {
		if (holder.is(k -> "minecraft".equals(k.identifier().getNamespace()))) {
			throw new IllegalArgumentException("May not register a fluid model to a vanilla fluid");
		}

		if (this.models.putIfAbsent(holder, model) != null) {
			throw new IllegalArgumentException("May not register duplicated fluid model");
		}
	}

	@Override
	public void registerRenderHandler(Holder<Fluid> holder, FluidRenderHandler renderHandler) {
		if (holder.is(k -> "minecraft".equalsIgnoreCase(k.identifier().getNamespace()))) {
			throw new IllegalArgumentException("May not register a fluid render handler to a vanilla fluid");
		}

		if (this.renderHandlers.putIfAbsent(holder, renderHandler) != null) {
			throw new IllegalArgumentException("May not register duplicated fluid render handler");
		}
	}
}
