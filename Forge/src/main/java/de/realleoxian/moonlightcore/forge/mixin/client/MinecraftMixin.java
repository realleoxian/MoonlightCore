package de.realleoxian.moonlightcore.forge.mixin.client;

import de.realleoxian.moonlightcore.api.client.event.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(
            method = "run",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;gameThread:Ljava/lang/Thread;",
                    shift = At.Shift.AFTER,
                    ordinal = 0,
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void moonlightcore$run(CallbackInfo ci) {
        ClientLifecycleEvents.STARTED.invoker().onClientStarted((Minecraft) (Object) this);
    }

    @Inject(
            method = "destroy",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/slf4j/Logger;info(Ljava/lang/String;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void moonlightcore$destroy(CallbackInfo ci) {
        ClientLifecycleEvents.STOPPING.invoker().onClientStopping((Minecraft) (Object) this);
    }
}
