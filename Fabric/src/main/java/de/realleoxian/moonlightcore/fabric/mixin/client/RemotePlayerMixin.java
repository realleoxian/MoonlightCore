package de.realleoxian.moonlightcore.fabric.mixin.client;

import de.realleoxian.moonlightcore.api.event.LivingEntityEvents;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RemotePlayer.class)
public class RemotePlayerMixin {
    @Inject(
            method = "hurt",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void moonlightcore$firePlayerAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(LivingEntityEvents.ATTACK.invoker().onEntityAttack(source, amount).isFalse()) {
            cir.setReturnValue(false);
        }
    }
}
