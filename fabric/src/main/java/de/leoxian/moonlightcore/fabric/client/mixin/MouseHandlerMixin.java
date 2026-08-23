package de.leoxian.moonlightcore.fabric.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.leoxian.moonlightcore.client.event.InputEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow
    private boolean isLeftPressed;

    @Shadow
    private boolean isMiddlePressed;

    @Shadow
    private boolean isRightPressed;

    @Shadow
    private double xpos;

    @Shadow
    private double ypos;

    @Inject(
            method = "onButton",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void moonlightcore$dispatchPreMouseInputEvent(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo ci) {
        if (handle == Minecraft.getInstance().getWindow().handle() &&
                InputEvents.PRE_MOUSE_INPUT.doFire().onPreMouseInput(rawButtonInfo.button(), rawButtonInfo.modifiers(), action).isDeny()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "onButton",
            at = @At(value = "RETURN")
    )
    private void moonlightcore$dispatchPostMouseInputEvent(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo ci) {
        if (handle == Minecraft.getInstance().getWindow().handle()) {
            InputEvents.POST_MOUSE_INPUT.doFire().onPostMouseInput(rawButtonInfo.button(), rawButtonInfo.modifiers(), action);
        }
    }

    @Inject(
            method = "onScroll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void moonlightcore$dispatchMouseScrollEvent(long handle, double xoffset, double yoffset, CallbackInfo ci, @Local boolean discreteScroll, @Local(ordinal = 0) double scrollSensitivity, @Local(ordinal = 1) double scaledXOffset, @Local(ordinal = 2) double scaledYOffset) {
        if (InputEvents.MOUSE_SCROLL.doFire().onMouseScroll(scaledXOffset, scaledYOffset, isLeftPressed, isMiddlePressed, isRightPressed, xpos, ypos).isDeny()) {
            ci.cancel();
        }
    }
}
