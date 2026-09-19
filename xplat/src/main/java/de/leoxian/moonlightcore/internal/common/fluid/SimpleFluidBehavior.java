package de.leoxian.moonlightcore.internal.common.fluid;

import de.leoxian.moonlightcore.common.fluid.FluidBehavior;
import de.leoxian.moonlightcore.mixin.accessor.LivingEntityAccessor;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiPredicate;

public record SimpleFluidBehavior(ToFloatFunction<LivingEntity> movementSpeed, Builder.MovementSlowdownFunction movementSlowdown, float gravityModifier, boolean canSwim, boolean canPushEntity, boolean canDrown, boolean supportsBoating, boolean canRiddenMobsFloat, BiPredicate<TagKey<Fluid>, LivingEntity> canSprint) implements FluidBehavior {
    public static final FluidBehavior DEFAULT = FluidBehavior.simple().build();
    public static final FluidBehavior WATER_LIKE = FluidBehavior.simple()
            .movementSpeed(entity -> {
                float speed = 0.02F;
                float waterWalker = (float) entity.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);

                if (!entity.onGround()) {
                    waterWalker *= 0.5F;
                }

                if (waterWalker > 0.0F) {
                    speed += (entity.getSpeed() - speed) * waterWalker;
                }
                return speed;
            })
            .movementSlowdown((entity, motion, _, baseGravity, isFalling) -> {
                float slowDown = entity.isSprinting() ? 0.9F : ((LivingEntityAccessor) entity).callGetWaterSlowDown();
                float waterWalker = (float) entity.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);

                if (!entity.onGround()) {
                    waterWalker *= 0.5F;
                }

                if (waterWalker > 0.0F) {
                    slowDown += (0.54600006F - slowDown) * waterWalker;
                }

                if (entity.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                    slowDown = 0.96F;
                }

                if (entity.horizontalCollision && entity.onClimbable()) {
                    motion = new Vec3(motion.x, 0.2, motion.z);
                }
                motion = motion.multiply(slowDown, 0.8F, slowDown);
                return entity.getFluidFallingAdjustedMovement(baseGravity, isFalling, motion);
            })
            .gravityModifier(0)
            .canDrown(true)
            .canSwim(true)
            .supportsBoating(true)
            .canSprint((fluid, entity) -> entity.isEyeInFluid(fluid))
            .build();

    @Override
    public void travelInFluid(TagKey<Fluid> fluid, LivingEntity entity, Vec3 input, double baseGravity, boolean isFalling, double oldY) {
        float speed = this.movementSpeed.applyAsFloat(entity);
        entity.moveRelative(speed, input);
        entity.move(MoverType.SELF, entity.getDeltaMovement());

        entity.setDeltaMovement(this.movementSlowdown.apply(entity, entity.getDeltaMovement(), entity.getFluidHeight(fluid) <= entity.getFluidJumpThreshold(), baseGravity, isFalling));

        if (baseGravity != 0.0F && this.gravityModifier != 0.0F) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0F, -baseGravity * this.gravityModifier, 0.0F));
        }

        ((LivingEntityAccessor) entity).callJumpOutOfFluid(oldY);

        if (this.canRiddenMobsFloat) {
            boolean canEntityFloatInWater = entity.is(EntityTypeTags.CAN_FLOAT_WHILE_RIDDEN);

            if (canEntityFloatInWater && entity.isVehicle() && entity.getFluidHeight(fluid) > entity.getFluidJumpThreshold()) {
                entity.setDeltaMovement(entity.getDeltaMovement().add(0.0F, 0.04F, 0.0F));
            }
        }
    }

    @Override
    public boolean canSwim(TagKey<Fluid> fluid, Entity entity) {
        return this.canSwim;
    }

    @Override
    public boolean canPushEntity(TagKey<Fluid> fluid, Entity entity) {
        return this.canPushEntity;
    }

    @Override
    public boolean canDrown(TagKey<Fluid> fluid, LivingEntity entity) {
        return this.canDrown;
    }

    @Override
    public boolean supportsBoating(TagKey<Fluid> fluid, Entity boat) {
        return this.supportsBoating;
    }

    @Override
    public boolean canSprint(TagKey<Fluid> fluid, LivingEntity entity) {
        return this.canSprint.test(fluid, entity);
    }

    public static final class BuilderImpl implements Builder {
        private ToFloatFunction<LivingEntity> movementSpeed = _ -> 0.02F;
        private MovementSlowdownFunction movementSlowdown = (_, movementDelta, _, _, _) -> movementDelta.multiply(0.65F, 0.65F, 0.65F);
        private float gravityModifier = 1.0F / 16.0F;
        private boolean canSwim = false;
        private boolean canPushEntity = false;
        private boolean canDrown = false;
        private boolean supportsBoating = false;
        private boolean canRiddenMobsFloat = false;
        private BiPredicate<TagKey<Fluid>, LivingEntity> canSprint = (_, _) -> true;

        @Override
        public Builder movementSpeed(ToFloatFunction<LivingEntity> function) {
            this.movementSpeed = function;
            return this;
        }

        @Override
        public Builder movementSpeed(float movementSpeed) {
            return this.movementSpeed(entity -> movementSpeed);
        }

        @Override
        public Builder movementSlowdown(MovementSlowdownFunction function) {
            this.movementSlowdown = function;
            return this;
        }

        @Override
        public Builder movementSlowdown(ToFloatFunction<LivingEntity> function) {
            return this.movementSlowdown((e, m, _, _, _) -> m.scale(function.applyAsFloat(e)));
        }

        @Override
        public Builder movementSlowdown(float horizontal, float vertical) {
            return this.movementSlowdown((_, m, _, _, _) -> m.multiply(horizontal, vertical, horizontal));
        }

        @Override
        public Builder movementSlowdown(float movementSlowdown) {
            return this.movementSlowdown((_, m, _, _, _) -> m.scale(movementSlowdown));
        }

        @Override
        public Builder gravityModifier(float gravityModifier) {
            this.gravityModifier = gravityModifier;
            return this;
        }

        @Override
        public Builder canSwim(boolean canSwim) {
            this.canSwim = canSwim;
            return this;
        }

        @Override
        public Builder canPushEntity(boolean canPushEntity) {
            this.canPushEntity = canPushEntity;
            return this;
        }

        @Override
        public Builder canDrown(boolean canDrown) {
            this.canDrown = canDrown;
            return this;
        }

        @Override
        public Builder supportsBoating(boolean supportsBoating) {
            this.supportsBoating = supportsBoating;
            return this;
        }

        @Override
        public Builder canSprint(BiPredicate<TagKey<Fluid>, LivingEntity> predicate) {
            this.canSprint = predicate;
            return this;
        }

        @Override
        public Builder canSprint(boolean canSprint) {
            return this.canSprint((_, _) -> canSprint);
        }

        @Override
        public FluidBehavior build() {
            return new SimpleFluidBehavior(this.movementSpeed, this.movementSlowdown, this.gravityModifier, this.canSwim, this.canPushEntity, this.canDrown, this.supportsBoating, this.canRiddenMobsFloat, this.canSprint);
        }
    }
}
