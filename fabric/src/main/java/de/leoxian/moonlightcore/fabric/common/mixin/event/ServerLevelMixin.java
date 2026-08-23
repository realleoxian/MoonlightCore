package de.leoxian.moonlightcore.fabric.common.mixin.event;

import de.leoxian.moonlightcore.common.event.VanillaGameEventCallback;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(
            method = "gameEvent",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void moonlightcore$dispatchVanillaGameEvent(Holder<GameEvent> gameEvent, Vec3 position, GameEvent.Context context, CallbackInfo ci) {
        if (VanillaGameEventCallback.EVENT.doFire().onVanillaGameEvent((ServerLevel) (Object) this, gameEvent, context, position).isDeny()) {
            ci.cancel();
        }
    }
}
