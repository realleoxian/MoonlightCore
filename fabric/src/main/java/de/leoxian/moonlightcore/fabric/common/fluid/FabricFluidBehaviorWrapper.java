package de.leoxian.moonlightcore.fabric.common.fluid;

import de.leoxian.moonlightcore.common.fluid.FluidAttributesHandler;
import de.leoxian.moonlightcore.common.fluid.FluidBehavior;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;

public record FabricFluidBehaviorWrapper(FluidAttributesHandler attributesHandler, FluidBehavior behavior) implements net.fabricmc.fabric.api.registry.fluid.FluidBehavior {
	@Override
	public void handleFluidInteractionUpdate(TagKey<Fluid> fluid, Entity entity, EntityFluidInteraction interaction, boolean canPushEntity) {
		if (canPushEntity) {
			interaction.applyCurrentTo(fluid, entity, 0.014F);
		}
	}

	@Override
	public void travelInFluid(TagKey<Fluid> fluid, LivingEntity entity, Vec3 input, double baseGravity, boolean isFalling, double oldY) {
		entity.fallDistance *= attributesHandler.getFallDistanceModifier(entity);
		behavior.travelInFluid(fluid, entity, input, baseGravity, isFalling, oldY);
	}

	@Override
	public boolean canSwimInFluid(TagKey<Fluid> fluid, Entity entity) {
		return behavior.canSwim(fluid, entity);
	}

	@Override
	public boolean canDrownInFluid(TagKey<Fluid> fluid, LivingEntity entity) {
		return behavior.canDrown(fluid, entity);
	}

	@Override
	public boolean canSupportBoat(TagKey<Fluid> fluid, Entity entity) {
		return behavior.supportsBoating(fluid, entity);
	}

	@Override
	public boolean canSprintInFluid(TagKey<Fluid> fluid, LivingEntity entity) {
		return behavior.canSprint(fluid, entity);
	}
}
