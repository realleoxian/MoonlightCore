package de.realleoxian.moonlightcore.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.realleoxian.moonlightcore.api.event.ServerPlayerEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(
            method = "changeDimension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/network/protocol/game/ClientboundLevelEventPacket;<init>(ILnet/minecraft/core/BlockPos;IZ)V",
                            shift = At.Shift.AFTER
                    )
            )
    )
    private void moonlightcore$firePlayerChangeDimension(ServerLevel destination, CallbackInfoReturnable<Entity> cir, @Local ServerLevel serverLevel) {
        ServerPlayerEvents.CHANGE_DIMENSION.invoker().onPlayerChangeDimension((ServerPlayer) (Object) this, serverLevel.dimension(), destination.dimension());
    }
}
