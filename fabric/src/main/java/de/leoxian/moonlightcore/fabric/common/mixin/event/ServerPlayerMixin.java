package de.leoxian.moonlightcore.fabric.common.mixin.event;

import com.llamalad7.mixinextras.sugar.Local;
import de.leoxian.moonlightcore.common.event.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(
            method = "die",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void moonlightcore$dispatchLivingDeathEvent(DamageSource source, CallbackInfo ci) {
        if (LivingDeathEvent.EVENT.doFire().onLivingDeath((ServerPlayer) (Object) this, source).isFalse()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$dispatchOpenMenuEvent(MenuProvider provider, CallbackInfoReturnable<OptionalInt> cir) {
        ServerPlayer player = ((ServerPlayer) (Object) this);
        ServerPlayerEvents.OPEN_MENU.doFire().onOpenMenu(player, player.containerMenu);
    }

    @Inject(
            method = "closeContainer",
            at = @At(value = "HEAD")
    )
    private void moonlightcore$dispatchCloseMenuEvent(CallbackInfo ci) {
        ServerPlayer player = ((ServerPlayer) (Object) this);
        ServerPlayerEvents.CLOSE_MENU.doFire().onCloseMenu(player, player.containerMenu);
    }

    @Inject(
            method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;teleportSpectators(Lnet/minecraft/world/level/portal/TeleportTransition;Lnet/minecraft/server/level/ServerLevel;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void moonlightcore$dispatchChangeDimensionEvent(TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir, @Local(ordinal = 0) ServerLevel oldLevel) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        ServerLevel newLevel = transition.newLevel();

        if (oldLevel != newLevel) {
            ServerPlayerEvents.CHANGE_DIMENSION.doFire().onChangeDimension(self, oldLevel.dimension(), newLevel.dimension());
        }
    }

    @Inject(
            method = "tick",
            at = @At(value = "HEAD")
    )
    private void moonlightcore$dispatchStartTickEvent(CallbackInfo ci) {
        ServerPlayerTickEvents.START.doFire().onServerPlayerTickStart((ServerPlayer) (Object) this);
    }

    @Inject(
            method = "tick",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$dispatchEndTickEvent(CallbackInfo ci) {
        ServerPlayerTickEvents.END.doFire().onServerPlayerTickEnd((ServerPlayer) (Object) this);
    }
}
