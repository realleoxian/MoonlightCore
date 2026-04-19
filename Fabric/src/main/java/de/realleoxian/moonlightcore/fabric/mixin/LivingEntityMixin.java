package de.realleoxian.moonlightcore.fabric.mixin;

import de.realleoxian.moonlightcore.api.event.LivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
            method = "hurt",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void moonlightcore$fireAttackEvent(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (LivingEntityEvents.ATTACK.invoker().onEntityAttack(source, amount).isFalse()) {
            cir.setReturnValue(false);
        }
    }
}
