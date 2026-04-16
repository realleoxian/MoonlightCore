package de.realleoxian.moonlightcore.forge.mixin;

import de.realleoxian.moonlightcore.api.event.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(
            method = "openMenu",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$fireOpenMenu$1(MenuProvider menu, CallbackInfoReturnable<OptionalInt> cir) {
        if(cir.getReturnValue().isPresent()) {
            ServerPlayerEvents.OPEN_MENU.invoker().onPlayerOpenMenu((ServerPlayer) (Object) this, ((ServerPlayer) (Object) this).containerMenu);
        }
    }

    @Inject(
            method = "openHorseInventory",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$fireOpenMenu$2(AbstractHorse horse, Container inventory, CallbackInfo ci) {
        ServerPlayerEvents.OPEN_MENU.invoker().onPlayerOpenMenu((ServerPlayer) (Object) this, ((ServerPlayer) (Object) this).containerMenu);
    }

    @Inject(
            method = "doCloseContainer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;removed(Lnet/minecraft/world/entity/player/Player;)V"
            )
    )
    private void moonlightcore$fireCloseMenu(CallbackInfo ci) {
        ServerPlayerEvents.CLOSE_MENU.invoker().onPlayerCloseMenu((ServerPlayer) (Object) this, ((ServerPlayer) (Object) this).containerMenu);
    }
}
