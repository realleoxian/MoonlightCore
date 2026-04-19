package de.realleoxian.moonlightcore.fabric.mixin;

import de.realleoxian.moonlightcore.api.event.ItemPickupEvent;
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
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getCount()I",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void moonlightcore$fireItemPickupEvent(Player player, CallbackInfo ci) {
        if (ItemPickupEvent.EVENT.invoker().onItemPickup(player, (ItemEntity) (Object) ItemEntityMixin.this).isFalse()) {
            ci.cancel();
        }
    }
}
