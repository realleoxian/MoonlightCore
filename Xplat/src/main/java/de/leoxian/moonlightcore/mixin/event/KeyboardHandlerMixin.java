package de.leoxian.moonlightcore.mixin.event;

import de.leoxian.moonlightcore.event.client.InputEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

     @Inject(method = "keyPress", at = @At("TAIL"))
     public void mlcore_onKeyInput(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
          if(windowPointer != Minecraft.getInstance().getWindow().getWindow()) {
               return;
          }

          InputEvent.KEY.invoker().onKeyInput(key, scanCode, action, modifiers);
     }

}
