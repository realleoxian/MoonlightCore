package de.leoxian.moonlightcore.client.gui;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface GuiLayerRegistrar {
    static void init(String namespace, Consumer<GuiLayerRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.guiLayers(namespace, initializer);
    }

    void registerBelowAll(Identifier id, GuiLayer layer);

    void registerBelow(Identifier upper, Identifier id, GuiLayer layer);

    void registerAbove(Identifier below, Identifier id, GuiLayer layer);

    void registerAboveAll(Identifier id, GuiLayer layer);

    void replaceLayer(Identifier id, GuiLayer replacement);
}
