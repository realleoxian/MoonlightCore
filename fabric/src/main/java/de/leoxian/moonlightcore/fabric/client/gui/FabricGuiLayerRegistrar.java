package de.leoxian.moonlightcore.fabric.client.gui;

import de.leoxian.moonlightcore.client.gui.GuiLayer;
import de.leoxian.moonlightcore.client.gui.GuiLayerRegistrar;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

public enum FabricGuiLayerRegistrar implements GuiLayerRegistrar {
    INSTANCE
    ;

    @Override
    public void registerBelowAll(Identifier id, GuiLayer layer) {
        HudElementRegistry.addLast(id, new FabricGuiLayer(layer));
    }

    @Override
    public void registerBelow(Identifier upper, Identifier id, GuiLayer layer) {
        HudElementRegistry.attachElementBefore(upper, id, new FabricGuiLayer(layer));
    }

    @Override
    public void registerAbove(Identifier below, Identifier id, GuiLayer layer) {
        HudElementRegistry.attachElementAfter(below, id, new FabricGuiLayer(layer));
    }

    @Override
    public void registerAboveAll(Identifier id, GuiLayer layer) {
        HudElementRegistry.addFirst(id, new FabricGuiLayer(layer));
    }

    @Override
    public void replaceLayer(Identifier id, GuiLayer replacement) {
        HudElementRegistry.replaceElement(id, r -> new FabricGuiLayer(replacement));
    }
}
