package de.leoxian.moonlightcore.fabric.common.fluid;

import de.leoxian.moonlightcore.common.fluid.FluidAttributesHandler;
import de.leoxian.moonlightcore.common.transfer.fluid.FluidResource;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public record FluidAttributeHandlerWrapper(FluidAttributesHandler handler) implements FluidVariantAttributeHandler {
	@Override
	public Component getName(FluidVariant fluidVariant) {
		return this.handler.getColoredName(FluidResource.of(fluidVariant.getFluid(), fluidVariant.getComponentsPatch()));
	}

	@Override
	public Optional<SoundEvent> getFillSound(FluidVariant variant) {
		return handler.getFillSound(FluidResource.of(variant.getFluid(), variant.getComponentsPatch()));
	}

	@Override
	public Optional<SoundEvent> getEmptySound(FluidVariant variant) {
		return handler.getEmptySound(FluidResource.of(variant.getFluid(), variant.getComponentsPatch()));
	}

	@Override
	public int getLightEmission(FluidVariant variant) {
		return handler.getLightEmission(FluidResource.of(variant.getFluid(), variant.getComponentsPatch()));
	}

	@Override
	public int getTemperature(FluidVariant variant) {
		return handler.getTemperature(FluidResource.of(variant.getFluid(), variant.getComponentsPatch()));
	}

	@Override
	public int getViscosity(FluidVariant variant, @Nullable Level level) {
		return handler.getViscosity(FluidResource.of(variant.getFluid(), variant.getComponentsPatch()));
	}
}
