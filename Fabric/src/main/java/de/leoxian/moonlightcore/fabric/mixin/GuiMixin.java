package de.leoxian.moonlightcore.fabric.mixin;

import de.leoxian.moonlightcore.event.client.HudRenderEvent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Unique
    private final HudRenderEvent.Context mlcore_ctx = new HudRenderEvent.Context() {};

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void mlcore_preRenderHUD(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        if(HudRenderEvent.PRE.invoker().onPreHudRendering(guiGraphics, guiGraphics.pose(), this.mlcore_ctx, partialTick).isFalse()) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void mlcore_postRenderHUD(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        HudRenderEvent.POST.invoker().onPostHudRendering(guiGraphics, guiGraphics.pose(), this.mlcore_ctx, partialTick);
    }

}
