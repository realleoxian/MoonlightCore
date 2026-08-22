package de.leoxian.moonlightcore.neoforge.common.mixin.event;

import de.leoxian.moonlightcore.common.event.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
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
}
