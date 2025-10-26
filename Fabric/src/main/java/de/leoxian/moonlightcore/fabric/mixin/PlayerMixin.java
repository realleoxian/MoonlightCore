package de.leoxian.moonlightcore.fabric.mixin;

import de.leoxian.moonlightcore.event.common.PlayerEvent;
import de.leoxian.moonlightcore.event.common.TickEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void mlcore_preTick(CallbackInfo ci) {
        TickEvent.PLAYER_TICK.invoker().onPlayerTick(TickEvent.Phase.START, (Player) (Object) this);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void mlcore_postTick(CallbackInfo ci) {
        TickEvent.PLAYER_TICK.invoker().onPlayerTick(TickEvent.Phase.END, (Player) (Object) this);
    }

    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"), cancellable = true)
    private void mlcore_drop(ItemStack itemStack, boolean includeThrowerName, CallbackInfoReturnable<ItemEntity> cir) {
        if(cir.getReturnValue() != null && PlayerEvent.DROP_ITEM.invoker().onDropItem((Player) (Object) this, cir.getReturnValue()).isFalse()) {
            cir.cancel();
        }
    }
}
