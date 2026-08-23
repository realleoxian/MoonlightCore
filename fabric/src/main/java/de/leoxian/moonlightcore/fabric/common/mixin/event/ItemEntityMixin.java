package de.leoxian.moonlightcore.fabric.common.mixin.event;

import de.leoxian.moonlightcore.common.event.ItemEntityPickupEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(
            method = "playerTouch",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true)
    private void moonlightcore$dispatcPreItemEntityPickupEvent(Player player, CallbackInfo ci) {
        if (ItemEntityPickupEvents.PRE.doFire().onPreItemEntityPickup(player, (ItemEntity) (Object) this).isFalse()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "playerTouch",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/stats/Stat;I)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void moonlightcore$dispatchPostItemEntityPickupEvent(Player player, CallbackInfo ci) {
        ItemEntityPickupEvents.POST.doFire().onPostItemEntityPickup(player, (ItemEntity) (Object) this);
    }
}
