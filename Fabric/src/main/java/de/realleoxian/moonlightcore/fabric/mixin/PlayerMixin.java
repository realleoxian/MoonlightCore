package de.realleoxian.moonlightcore.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.realleoxian.moonlightcore.api.event.ItemTossEvents;
import de.realleoxian.moonlightcore.api.event.LivingEntityEvents;
import de.realleoxian.moonlightcore.api.event.PlayerTickEvents;
import net.minecraft.world.damagesource.DamageSource;
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
    @Inject(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/item/ItemEntity;setPickUpDelay(I)V"
            ),
            cancellable = true
    )
    private void moonlightcore$firePreItemToss(ItemStack droppedItem, boolean dropAround, boolean includeThrowerName, CallbackInfoReturnable<ItemEntity> cir, @Local ItemEntity itemEntity) {
        if (ItemTossEvents.PRE_ITEM_TOSS.invoker().onPreItemToss((Player) (Object) this, itemEntity).isFalse()) {
            cir.setReturnValue(null);
        }
    }

    @Inject(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$fireItemToss(ItemStack droppedItem, boolean dropAround, boolean includeThrowerName, CallbackInfoReturnable<ItemEntity> cir) {
        ItemTossEvents.POST_ITEM_TOSS.invoker().onItemToss((Player) (Object) this, cir.getReturnValue());
    }

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

    @Inject(
            method = "tick",
            at = @At(value = "HEAD")
    )
    private void moonlightcore$firePlayerTickStart(CallbackInfo ci) {
        PlayerTickEvents.TICK_START.invoker().onStartPlayerTick((Player) (Object) this);
    }

    @Inject(
            method = "tick",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$firePlayerTickEnd(CallbackInfo ci) {
        PlayerTickEvents.TICK_START.invoker().onStartPlayerTick((Player) (Object) this);
    }
}
