package de.leoxian.moonlightcore.mixin.client;

import de.leoxian.moonlightcore.api.client.event.InputEvents;
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
    private void moonlightcore$onPress(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
        if (windowPointer != Minecraft.getInstance().getWindow().getWindow())
            return;

        InputEvents.MOUSE_INPUT.invoker().onMouseInput(button, action, modifiers);
    }
}
