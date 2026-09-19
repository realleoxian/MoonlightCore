package de.leoxian.moonlightcore.common.pack;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ResourceReloadListenerRegistrar {
    /// Configure and register modded resource reload listeners
    /// @param initializer The initializer
    static void configure(Consumer<ResourceReloadListenerRegistrar> initializer) {
        XplatAbstraction.INSTANCE.serverReloadListeners(initializer);
    }

    /// Registers a new server reloadable listener with access to the registry
    /// @param id The id of the listener
    /// @param listener The listener factory
    void register(Identifier id, Function<HolderLookup.Provider, PreparableReloadListener> listener);

    /// Registers a new server reloadable listener that doesn't need registry access
    /// @param id The id of the listener
    /// @param listener The listener's instance
    default void register(Identifier id, PreparableReloadListener listener) {
        register(id, _ -> listener);
    }

    /// Adds a new dependency order, such that `first` must run before `second`
    /// @param first The key of the reload listener that must run first
    /// @param second The key of the reload listener that must run after first
    void addDependency(Identifier first, Identifier second);
}
