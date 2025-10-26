package de.leoxian.moonlightcore.fabric.mixin.client;

import de.leoxian.moonlightcore.event.common.EntityEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void mlcore_addEntity(int entityId, Entity entityToSpawn, CallbackInfo ci) {
        if(EntityEvent.ADDITION.invoker().onEntityAddition((ClientLevel) (Object) this, entityToSpawn).isFalse()) {
            ci.cancel();
        }
    }

}
