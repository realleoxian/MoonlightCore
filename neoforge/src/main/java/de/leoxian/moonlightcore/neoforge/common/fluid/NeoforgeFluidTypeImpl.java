package de.leoxian.moonlightcore.neoforge.common.fluid;

import de.leoxian.moonlightcore.common.fluid.FluidBehavior;
import de.leoxian.moonlightcore.common.fluid.FluidAttributesHandler;
import de.leoxian.moonlightcore.common.transfer.fluid.FluidResource;
import de.leoxian.moonlightcore.mixin.accessor.LivingEntityAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.SoundAction;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jspecify.annotations.Nullable;

public class NeoforgeFluidTypeImpl extends FluidType {
	private final TagKey<Fluid> tagKey;
	private final FluidAttributesHandler propertiesHandler;
	private final FluidBehavior entityInteraction;

	public NeoforgeFluidTypeImpl(TagKey<Fluid> tagKey, FluidAttributesHandler propertiesHandler, FluidBehavior entityInteraction) {
		super(Properties.create());
		this.tagKey = tagKey;
		this.propertiesHandler = propertiesHandler;
		this.entityInteraction = entityInteraction;
	}

	@Override
	public boolean move(LivingEntity entity, Vec3 movementVector, double gravity) {
		boolean isFalling = entity.getDeltaMovement().y <= (double) 0.0F;
		double oldY = entity.getY();
		double baseGravity = ((LivingEntityAccessor) entity).callGetEffectiveGravity();

		float fallModifier = this.propertiesHandler.getFallDistanceModifier(entity);
		entity.fallDistance *= fallModifier;
		this.entityInteraction.travelInFluid(this.tagKey, entity, movementVector, baseGravity, isFalling, oldY);
		return true;
	}

	@Override
	public boolean canSwim(Entity entity) {
		return this.entityInteraction.canSwim(this.tagKey, entity);
	}

	@Override
	public boolean canDrownIn(LivingEntity entity) {
		return this.entityInteraction.canDrown(this.tagKey, entity);
	}

	@Override
	public boolean supportsBoating(AbstractBoat boat) {
		return this.entityInteraction.supportsBoating(this.tagKey, boat);
	}

	@Override
	public boolean canPushEntity(Entity entity) {
		return this.entityInteraction.canPushEntity(this.tagKey, entity);
	}

	@Override
	public Component getDescription(FluidStack stack) {
		FluidResource resource = FluidResource.of(stack.getFluid(), stack.getComponentsPatch());
		return this.propertiesHandler.getColoredName(resource);
	}

	@Override
	public @Nullable SoundEvent getSound(FluidStack stack, SoundAction action) {
		FluidResource resource = FluidResource.of(stack.getFluid(), stack.getComponentsPatch());
		SoundEvent fallback = super.getSound(stack, action);
		if (action == SoundActions.BUCKET_EMPTY) {
			return this.propertiesHandler.getEmptySound(resource).orElse(fallback);
		} else if (action == SoundActions.BUCKET_FILL) {
			return this.propertiesHandler.getFillSound(resource).orElse(fallback);
		}
		return fallback;
	}

	@Override
	public int getLightLevel(FluidStack stack) {
		FluidResource resource = FluidResource.of(stack.getFluid(), stack.getComponentsPatch());
		return this.propertiesHandler.getLightEmission(resource);
	}

	@Override
	public int getTemperature(FluidStack stack) {
		FluidResource resource = FluidResource.of(stack.getFluid(), stack.getComponentsPatch());
		return this.propertiesHandler.getTemperature(resource);
	}

	@Override
	public int getViscosity(FluidStack stack) {
		FluidResource resource = FluidResource.of(stack.getFluid(), stack.getComponentsPatch());
		return this.propertiesHandler.getViscosity(resource);
	}
}
