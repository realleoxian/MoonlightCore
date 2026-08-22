package de.leoxian.moonlightcore.neoforge.client.gui;

import de.leoxian.moonlightcore.client.gui.GuiLayer;
import de.leoxian.moonlightcore.client.gui.GuiLayerRegistrar;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

public record NeoforgeGuiLayerRegistrar(RegisterGuiLayersEvent event) implements GuiLayerRegistrar {
    @Override
    public void registerBelowAll(Identifier id, GuiLayer layer) {
        event.registerBelowAll(id, new NeoforgeGuiLayer(layer));
    }

    @Override
    public void registerBelow(Identifier upper, Identifier id, GuiLayer layer) {
        event.registerBelow(id, upper, new NeoforgeGuiLayer(layer));
    }

    @Override
    public void registerAbove(Identifier below, Identifier id, GuiLayer layer) {
        event.registerAbove(id, below, new NeoforgeGuiLayer(layer));
    }

    @Override
    public void registerAboveAll(Identifier id, GuiLayer layer) {
        event.registerAboveAll(id, new NeoforgeGuiLayer(layer));
    }

    @Override
    public void replaceLayer(Identifier id, GuiLayer replacement) {
        event.replaceLayer(id, new NeoforgeGuiLayer(replacement));
    }
}
