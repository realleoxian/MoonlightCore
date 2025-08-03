package de.leowgc.moonlightcore.mixin.client;

import de.leowgc.moonlightcore.api.event.EventDispatcher;
import de.leowgc.moonlightcore.api.event.client.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onPress", at = @At("TAIL"))
    public void mlcore_onPress(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
        if(Minecraft.getInstance().getWindow().getWindow() != windowPointer) {
            return;
        }

        EventDispatcher.INSTANCE.fire(InputEvent.MOUSE_INPUT, (listener) -> listener.bootstrap(button, action, modifiers));
    }

}
