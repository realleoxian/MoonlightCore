package de.leowgc.moonlightcore.mixin.client;

import de.leowgc.moonlightcore.api.event.EventDispatcher;
import de.leowgc.moonlightcore.api.event.client.InputEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Inject(method = "keyPress", at = @At("TAIL"))
    public void mlcore_keyPress(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if(Minecraft.getInstance().getWindow().getWindow() != windowPointer) {
            return;
        }

        EventDispatcher.INSTANCE.fire(InputEvent.KEY_INPUT, (listener) -> listener.bootstrap(key, scanCode, action, modifiers));
    }

}
