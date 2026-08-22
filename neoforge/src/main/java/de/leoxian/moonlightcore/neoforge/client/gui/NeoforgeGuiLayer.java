package de.leoxian.moonlightcore.neoforge.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.neoforged.neoforge.client.gui.GuiLayer;

public record NeoforgeGuiLayer(de.leoxian.moonlightcore.client.gui.GuiLayer layer) implements GuiLayer {
    @Override
    public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        layer.extractRenderState(guiGraphics, deltaTracker);
    }
}
