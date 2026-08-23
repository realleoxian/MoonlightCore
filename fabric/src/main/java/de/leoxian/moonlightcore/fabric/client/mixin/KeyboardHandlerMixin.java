package de.leoxian.moonlightcore.fabric.client.mixin;

import de.leoxian.moonlightcore.client.event.InputEvents;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(
            method = "keyPress",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void moonlightcore$dispatchPreKeyInput(long handle, int action, KeyEvent event, CallbackInfo ci) {
        if (handle == Minecraft.getInstance().getWindow().handle()) {
            if (InputEvents.PRE_KEY_PRESS.doFire().onPreMouseInput(event.key(), event.scancode(), event.modifiers(), event.input()).isDeny()) {
                ci.cancel();
            }
        }
    }

    @Inject(
            method = "keyPress",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$dispatchPostKeyInput(long handle, int action, KeyEvent event, CallbackInfo ci) {
        if (handle == Minecraft.getInstance().getWindow().handle()) {
            InputEvents.POST_KEY_PRESS.doFire().onPostKeyInput(event.key(), event.scancode(), event.modifiers(), event.input());
        }
    }
}
