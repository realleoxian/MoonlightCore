package de.leoxian.moonlightcore.fabric.client.fluid;

import de.leoxian.moonlightcore.client.fluid.FluidRenderHandler;
import de.leoxian.moonlightcore.client.fluid.FluidRendererRegistrar;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.core.Holder;
import net.minecraft.world.level.material.Fluid;

public enum FabricFluidRendererRegistrar implements FluidRendererRegistrar {
	INSTANCE
	;

	@Override
	public void registerModel(Holder<Fluid> holder, FluidModel.Unbaked model) {
		if (holder.is(k -> "minecraft".equals(k.identifier().getNamespace()))) {
			throw new IllegalArgumentException("May not register a fluid model to a vanilla fluid");
		}
		FluidRenderingRegistry.register(holder.value(), model);
	}

	@Override
	public void registerRenderHandler(Holder<Fluid> holder, FluidRenderHandler renderHandler) {
		if (holder.is(k -> "minecraft".equals(k.identifier().getNamespace()))) {
			throw new IllegalArgumentException("May not register a fluid render handler to a vanilla fluid");
		}
		FluidVariantRendering.register(holder.value(), new FabricFluidRenderHandlerImpl(renderHandler));
	}
}
