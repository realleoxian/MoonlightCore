package de.leoxian.moonlightcore.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface GuiLayer {

     /// Renders the HUD element.
     /// @param guiGraphics the {@link GuiGraphicsExtractor} used for rendering
     /// @param deltaTracker the {@link DeltaTracker} providing timing information
    void extractRenderState(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker);
}
