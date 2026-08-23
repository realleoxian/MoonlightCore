package de.leoxian.moonlightcore.fabric.common.mixin.event;

import de.leoxian.moonlightcore.common.event.EntityInvulnerabilityCheckEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract boolean isRemoved();

    @Shadow
    private boolean invulnerable;

    @Shadow
    public abstract boolean fireImmune();

    @Inject(
            method = "isInvulnerableToBase",
            at = @At(value = "HEAD")
    )
    private void moonlightcore$dispatchEntityInvulnerabilityCheckEvent(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        boolean vanillaInvulnerable = this.isRemoved()
                || this.invulnerable && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !source.isCreativePlayer()
                || source.is(DamageTypeTags.IS_FIRE) && this.fireImmune()
                || source.is(DamageTypeTags.IS_FALL) && ((Entity) (Object) this).is(EntityTypeTags.FALL_DAMAGE_IMMUNE);
        cir.setReturnValue(EntityInvulnerabilityCheckEvent.EVENT.doFire().onEntityInvulnerabilityCheck((Entity) (Object) this, source, vanillaInvulnerable).isTrue());
    }
}
