package de.leoxian.moonlightcore.fabric.mixin;

import de.leoxian.moonlightcore.event.common.PlayerEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "restoreFrom", at = @At("RETURN"))
    private void mlcore_restoreFrom(ServerPlayer that, boolean keepEverything, CallbackInfo ci) {
        PlayerEvent.CLONE.invoker().onPlayerClone(that, (ServerPlayer) (Object) this);
    }

    @Inject(method = "openMenu", at = @At("RETURN"))
    private void mlcore_openMenu(MenuProvider menu, CallbackInfoReturnable<OptionalInt> cir) {
        if(cir.getReturnValue().isPresent()) {
            PlayerEvent.OPEN_MENU.invoker().onOpenMenu((ServerPlayer) (Object) this, ((ServerPlayer) (Object) this).containerMenu);
        }
    }

    @Inject(method = "openHorseInventory", at = @At("RETURN"))
    private void mlcore_openHorseInventory(AbstractHorse horse, Container inventory, CallbackInfo ci) {
        PlayerEvent.OPEN_MENU.invoker().onOpenMenu((ServerPlayer) (Object) this, ((ServerPlayer) (Object) this).containerMenu);
    }

    @Inject(method = "doCloseContainer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;removed(Lnet/minecraft/world/entity/player/Player;)V"))
    private void mlcore_doCloseConainer(CallbackInfo ci) {
        PlayerEvent.CLOSE_MENU.invoker().onCloseMenu((ServerPlayer) (Object) this, ((ServerPlayer) (Object) this).containerMenu);
    }

    @Inject(method = "triggerDimensionChangeTriggers", at = @At("HEAD"))
    private void mlcore_changeDimension(ServerLevel level, CallbackInfo ci) {
        PlayerEvent.CHANGE_DIMENSION.invoker().onChangeDimension((ServerPlayer) (Object) this, level.dimension(), ((ServerPlayer) (Object) this).level().dimension());
    }

}
