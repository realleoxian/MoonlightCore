package de.leoxian.moonlightcore.client.pack;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.Consumer;

public interface ClientResourceReloadListenerRegistrar {
    /// Configure and register client resource reload listeners
    /// @param namespace The mod's id to add this registrar to
    /// @param initializer The initializer of the registrar
    static void configure(String namespace, Consumer<ClientResourceReloadListenerRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.resourceReloadListeners(namespace, initializer);
    }

    /// Register the given reload listener
    /// @param id The listener identifier
    /// @param listener The listener
    void register(Identifier id, PreparableReloadListener listener);

    /// Adds a new dependency order, such that `first` must run before `second`
    /// @param first The key of the reload listener that must run first
    /// @param second The key of the reload listener that must run after first
    void addDependency(Identifier first, Identifier second);
}
