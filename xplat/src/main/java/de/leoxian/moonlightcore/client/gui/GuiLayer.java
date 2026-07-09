package de.leoxian.moonlightcore.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface GuiLayer {
    void extractRenderState(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker);
}
