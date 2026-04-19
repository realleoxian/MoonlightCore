package de.realleoxian.moonlightcore.fabric.mixin.client;

import de.realleoxian.moonlightcore.api.client.event.InputEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(
            method = "onPress",
            at = @At(
                    value = "HEAD"
            )
    )
    private void moonlightcore$fireMouseInput(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
        if (windowPointer == Minecraft.getInstance().getWindow().getWindow()) {
            InputEvents.MOUSE_INPUT.invoker().onMouseInput(button, action, modifiers);
        }
    }
}
