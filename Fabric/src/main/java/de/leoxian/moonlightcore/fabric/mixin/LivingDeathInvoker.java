package de.leoxian.moonlightcore.fabric.mixin;

import de.leoxian.moonlightcore.event.common.EntityEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {LivingEntity.class, Player.class, ServerPlayer.class})
public class LivingDeathInvoker {

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void mlcore_die(DamageSource damageSource, CallbackInfo ci) {
        if(EntityEvent.LIVING_DEATH.invoker().onEntityDeath((LivingEntity) (Object) this, damageSource).isFalse()) {
            ci.cancel();
        }
    }
}
