package de.leoxian.moonlightcore.neoforge.client.fluid;

import de.leoxian.moonlightcore.client.fluid.FluidRenderHandler;
import de.leoxian.moonlightcore.common.transfer.fluid.FluidResource;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Vector4f;

public record NeoforgeFluidRenderHandler(Holder<Fluid> fluid, FluidRenderHandler handler) implements IClientFluidTypeExtensions {
	@Override
	public void modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
		FluidResource resource = FluidResource.of(this.fluid.value());
		int color = this.handler.getColor(resource, level, camera.blockPosition());
		if (color != -1) {
			float red = ((color >> 16) & 0xFF) / 255.0F;
			float green = ((color >> 8) & 0xFF) / 255.0F;
			float blue = (color & 0xFF) / 255.0F;
			fluidFogColor.set(red, green, blue, 1.0F);
		}
	}
}
