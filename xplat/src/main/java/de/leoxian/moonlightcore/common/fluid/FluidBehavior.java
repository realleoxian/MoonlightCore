package de.leoxian.moonlightcore.common.fluid;

import de.leoxian.moonlightcore.internal.common.fluid.SimpleFluidBehavior;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiPredicate;

public interface FluidBehavior {
    FluidBehavior DEFAULT = SimpleFluidBehavior.DEFAULT;
    FluidBehavior WATER_LIKE = SimpleFluidBehavior.WATER_LIKE;

    static Builder simple() {
        return new SimpleFluidBehavior.BuilderImpl();
    }

    void travelInFluid(TagKey<Fluid> fluid, LivingEntity entity, Vec3 input, double baseGravity, boolean isFalling, double oldY);

    default boolean canSwim(TagKey<Fluid> fluid, Entity entity) {
        return false;
    }

    default boolean canPushEntity(TagKey<Fluid> fluid, Entity entity) {
        return false;
    }

    default boolean canDrown(TagKey<Fluid> fluid, LivingEntity entity) {
        return false;
    }

    default boolean supportsBoating(TagKey<Fluid> fluid, Entity boat) {
        return false;
    }

    default boolean canSprint(TagKey<Fluid> fluid, LivingEntity entity)  {
        return false;
    }

    interface Builder {
        Builder movementSpeed(ToFloatFunction<LivingEntity> function);

        Builder movementSpeed(float movementSpeed);

        Builder movementSlowdown(MovementSlowdownFunction function);

        Builder movementSlowdown(ToFloatFunction<LivingEntity> function);

        Builder movementSlowdown(float horizontal, float vertical);

        Builder movementSlowdown(float movementSlowdown);

        Builder gravityModifier(float gravityModifier);

        Builder canSwim(boolean canSwim);

        Builder canPushEntity(boolean canPushEntity);

        Builder canDrown(boolean canDrown);

        Builder supportsBoating(boolean supportsBoating);

        Builder canSprint(BiPredicate<TagKey<Fluid>, LivingEntity> predicate);

        Builder canSprint(boolean canSprint);

        FluidBehavior build();

        @FunctionalInterface
        interface MovementSlowdownFunction {
            Vec3 apply(LivingEntity entity, Vec3 movementDelta, boolean isBelowJumpThreshold, double baseGravity, boolean isFalling);
        }
    }
}
