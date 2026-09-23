package de.leoxian.moonlightcore.common.fluid;

import de.leoxian.moonlightcore.common.transfer.fluid.FluidResource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

public interface FluidAttributesHandler {
	FluidAttributesHandler DEFAULT = new FluidAttributesHandler() {};

	/// @param resource The fluid
	/// @return The display name of the fluid
	default Component getName(FluidResource resource) {
		Block fluidBlock = resource.fluid().defaultFluidState().createLegacyBlock().getBlock();

		if (!resource.isEmpty() && fluidBlock == Blocks.AIR) {
			return Component.translatable(Util.makeDescriptionId("block", BuiltInRegistries.FLUID.getKey(resource.fluid())));
		} else {
			return fluidBlock.getName();
		}
	}

	/// @param resource The fluid
	/// @return The color associated with the fluid
	default int getAssociatedColor(FluidResource resource) {
		return -1;
	}

	/// @param resource The fluid
	/// @return The display name of the fluid colored with it's associated color
	default Component getColoredName(FluidResource resource) {
		return getName(resource).copy().withColor(getAssociatedColor(resource));
	}

	/// @param resource The fluid
	/// @return The fill sound used for buckets
	default Optional<SoundEvent> getFillSound(FluidResource resource) {
		return Optional.empty();
	}

	/// @param resource The fluid
	/// @return The empty sound used for buckets
	default Optional<SoundEvent> getEmptySound(FluidResource resource) {
		return Optional.empty();
	}

	/// @param entity The entity that's falling
	/// @return The fall distance modifier of the fluid. By default, `0.0`
	default float getFallDistanceModifier(Entity entity) {
		return 0.0F;
	}

	/// @param resource The fluid
	/// @return The light emission level of the fluid
	default int getLightEmission(FluidResource resource) {
		return resource.fluid().defaultFluidState().createLegacyBlock().getLightEmission();
	}

	/// @param resource The fluid
	/// @return The temperature of the fluid. By default, being `300`
	default int getTemperature(FluidResource resource) {
		return 300;
	}

	/// @param resource The fluid
	/// @return The viscosity of the fluid. By default, being `1000`
	default int getViscosity(FluidResource resource) {
		return 1000;
	}
}
