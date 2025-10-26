package de.leoxian.moonlightcore.fabric.mixin;

import de.leoxian.moonlightcore.event.common.VanillaGameEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "gameEvent", at = @At("HEAD"), cancellable = true)
    private void mlcore_gameEvent(GameEvent event, Vec3 position, GameEvent.Context context, CallbackInfo ci) {
        if(VanillaGameEvent.EVENT.invoker().onGameEvent((ServerLevel) (Object) this, event, position, context).isFalse()) {
            ci.cancel();
        }
    }
}
