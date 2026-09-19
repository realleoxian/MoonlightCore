package de.leoxian.moonlightcore.client.gui;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface GuiLayerRegistrar {
    /// Configure and register layers to the GUI
    /// @param namespace The mod's id to add this registrar to
    /// @param initializer The initializer of the registrar
    static void configure(String namespace, Consumer<GuiLayerRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.guiLayers(namespace, initializer);
    }

    /// Adds a GUI layer that renders below all others
    /// @param id The identifier of the layer
    /// @param layer The gui layer
    void registerBelowAll(Identifier id, GuiLayer layer);

    /// Adds a GUI layer that renders below a specific layer
    /// @param upper The layer that renders above the registered one
    /// @param id The registered layer identifier
    /// @param layer The gui layer
    void registerBelow(Identifier upper, Identifier id, GuiLayer layer);

    /// Adds a GUI layer that renders above other gui layer
    /// @param below The layer that renders below the registered one
    /// @param id The registered layer identifier
    /// @param layer The gui layer
    void registerAbove(Identifier below, Identifier id, GuiLayer layer);

    /// Adds a GUI layer that renders above all others
    /// @param id The identifier of the layer
    /// @param layer The layer
    void registerAboveAll(Identifier id, GuiLayer layer);

    /// Replaces an existing layer with other one
    /// @param id The identifier of the replaced layer
    /// @param replacement The replacement gui layer
    void replaceLayer(Identifier id, GuiLayer replacement);
}
