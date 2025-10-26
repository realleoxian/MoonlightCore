package de.leoxian.moonlightcore.fabric.mixin;

import de.leoxian.moonlightcore.event.common.EntityEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void mlcore_hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if((Object) this instanceof Player) return;

        if(EntityEvent.LIVING_HURT.invoker().onEntityHurt((LivingEntity) (Object) this, source, amount).isFalse()) {
            cir.setReturnValue(false);
        }
    }
}
