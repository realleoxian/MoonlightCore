package de.realleoxian.moonlightcore.fabric.mixin.client;

import de.realleoxian.moonlightcore.api.client.event.GuiRenderEvents;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(
            method = "render",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void moonlightcore$fireGuiRenderStart(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        if (GuiRenderEvents.GUI_RENDER_START.invoker().onGuiRenderStart(guiGraphics, partialTick).isFalse()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "RETURN"
            )
    )
    private void moonlightcore$fireGuiRenderEnd(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        GuiRenderEvents.GUI_RENDER_END.invoker().onGuiRenderEnd(guiGraphics, partialTick);
    }
}
