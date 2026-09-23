package de.leoxian.moonlightcore.fabric.client.fluid;

import de.leoxian.moonlightcore.client.fluid.FluidRenderHandler;
import de.leoxian.moonlightcore.common.transfer.fluid.FluidResource;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record FabricFluidRenderHandlerImpl(FluidRenderHandler handler) implements FluidVariantRenderHandler {
	@Override
	public void appendTooltip(FluidVariant fluidVariant, List<Component> tooltip, TooltipFlag tooltipFlag) {
		FluidResource resource = FluidResource.of(fluidVariant.getFluid(), fluidVariant.getComponentsPatch());
		handler.appendTooltip(resource, tooltip, tooltipFlag);
	}

	@Override
	public int getColor(FluidVariant fluidVariant, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
		FluidResource resource = FluidResource.of(fluidVariant.getFluid(), fluidVariant.getComponentsPatch());
		return handler.getColor(resource, level, pos);
	}
}
