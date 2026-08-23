package de.leoxian.moonlightcore.fabric.common.mixin.event;

import de.leoxian.moonlightcore.common.event.LivingDeathEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(
            method = "die",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void moonlightcore$dispatchLivingDeathEvent(DamageSource source, CallbackInfo ci) {
        if (LivingDeathEvent.EVENT.doFire().onLivingDeath((Player) (Object) this, source).isFalse()) {
            ci.cancel();
        }
    }
}
