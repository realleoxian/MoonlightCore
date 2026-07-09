package de.leoxian.moonlightcore.client.gui;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface GuiLayerRegistrar {
    void registerBelowAll(Identifier id, GuiLayer layer);

    void registerBelow(Identifier upper, Identifier id, GuiLayer layer);

    void registerAbove(Identifier below, Identifier id, GuiLayer layer);

    void registerAboveAll(Identifier id, GuiLayer layer);

    void replaceLayer(Identifier id, GuiLayer replacement);
}
