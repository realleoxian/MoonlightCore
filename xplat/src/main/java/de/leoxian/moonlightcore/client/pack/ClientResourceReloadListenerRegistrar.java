package de.leoxian.moonlightcore.client.pack;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.Function;

public interface ClientResourceReloadListenerRegistrar {
    void register(Identifier id, Function<HolderLookup.Provider, PreparableReloadListener> listenerFactory);

    void addDependency(Identifier first, Identifier second);
}
