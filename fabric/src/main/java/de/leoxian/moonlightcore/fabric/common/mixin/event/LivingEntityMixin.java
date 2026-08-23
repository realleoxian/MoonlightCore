package de.leoxian.moonlightcore.fabric.common.mixin.event;

import com.llamalad7.mixinextras.sugar.Local;
import de.leoxian.moonlightcore.common.event.ItemTossEvent;
import de.leoxian.moonlightcore.common.event.LivingDamageEvents;
import de.leoxian.moonlightcore.common.event.LivingDeathEvent;
import de.leoxian.moonlightcore.common.event.LivingUseItemEvents;
import de.leoxian.moonlightcore.common.event.base.CompoundEventResult;
import de.leoxian.moonlightcore.fabric.common.event.CommonEventHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract int getUseItemRemainingTicks();

    @Shadow
    public abstract InteractionHand getUsedItemHand();

    @Shadow
    protected int useItemRemaining;

    @Inject(
            method = "doHurtEquipment",
            at = @At("HEAD"),
            cancellable = true)
    private void moonlightcore$dispatchArmorHurtEvent(DamageSource damageSource, float damage, EquipmentSlot[] slots, CallbackInfo ci) {
        if (damage > 0.0F && slots.length > 0) {
            int durabilityDamage = (int) Math.max(1.0F, damage / 4.0F);

            CommonEventHooks.onArmorHurt(damageSource, slots, (float) durabilityDamage, (LivingEntity) (Object) this);
            ci.cancel();
        }
    }

    @Inject(
            method = "drop",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void moonlightcore$dispatchItemTossEvent(ItemStack itemStack, boolean randomly, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir, @Local ItemEntity entity) {
        if (ItemTossEvent.EVENT.doFire().onItemToss((LivingEntity) (Object) this, entity).isFalse()) {
            cir.cancel();
        }
    }

    @Inject(
            method = "actuallyHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;isInvulnerableTo(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)Z",
                    shift = At.Shift.AFTER
            )
    )
    private void moonlightcore$dispatchPreLivingDamageEvent(ServerLevel level, DamageSource source, float dmg, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!entity.isInvulnerableTo(level, source)) {
            LivingDamageEvents.PRE.doFire().onPreLivingDamage(entity, source, dmg);
        }
    }

    @Inject(
            method = "actuallyHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V",
                    shift = At.Shift.AFTER
            )
    )
    private void moonlightcore$dispatchPostLivingDamageEvent(ServerLevel level, DamageSource source, float dmg, CallbackInfo ci) {
        LivingDamageEvents.POST.doFire().onPostLivingDamage((LivingEntity) (Object) this, source);
    }

    @Inject(
            method = "die",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void moonlightcore$dispatchLivingDeathEvent(DamageSource source, CallbackInfo ci) {
        if (LivingDeathEvent.EVENT.doFire().onLivingDeath((LivingEntity) (Object) this, source).isFalse()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "startUsingItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;isUsingItem()Z",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void moonlightcore$dispatchStartItemUseEvent(InteractionHand hand, CallbackInfo ci, @Local ItemStack itemStack) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!LivingUseItemEvents.START.doFire().onItemUseStart(self, itemStack, hand, itemStack.getUseDuration(self)).isFalse()) {
            ci.cancel();
        }
    }

    @Redirect(
            method = "completeUsingItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack moonlightcore$dispatchFinishItemUseEvent(ItemStack instance, Level level, LivingEntity livingEntity) {
        CompoundEventResult<ItemStack> result = LivingUseItemEvents.FINISH.doFire().onItemUseFinish(livingEntity, instance, instance.getUseDuration(livingEntity));
        if (result.result().isSuccess() && result.isValuePresent()) {
            return result.value();
        }
        return instance;
    }

    @Inject(
            method = "releaseUsingItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isSameItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void moonlightcore$dispatchStopItemUseEvent(CallbackInfo ci, @Local ItemStack itemInUsedHand) {
        if (LivingUseItemEvents.STOP.doFire().onItemUseStop((LivingEntity) (Object) this, itemInUsedHand, getUsedItemHand(), getUseItemRemainingTicks()).isDeny()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "updateUsingItem",
            at = @At(value = "HEAD")
    )
    private void moonlightcore$dispatchTickItemUseEvent(ItemStack useItem, CallbackInfo ci) {
        if (!useItem.isEmpty()) {
            this.useItemRemaining = LivingUseItemEvents.TICK.doFire().onItemUseTick((LivingEntity) (Object) this, useItem, getUsedItemHand(), getUseItemRemainingTicks()).isDeny()
                    ? -1 : getUseItemRemainingTicks();
        }
    }
}
