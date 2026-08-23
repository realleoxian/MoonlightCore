package de.leoxian.moonlightcore.fabric.client.gui;

import de.leoxian.moonlightcore.client.gui.GuiLayer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public record FabricGuiLayer(GuiLayer layer) implements HudElement {
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        layer.extractRenderState(graphics, deltaTracker);
    }
}
