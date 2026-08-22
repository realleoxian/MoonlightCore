package de.leoxian.moonlightcore.neoforge.common.pack;

import de.leoxian.moonlightcore.common.pack.ResourceReloadListenerRegistrar;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

public record NeoforgeResourceReloadListenerRegistrar(AddServerReloadListenersEvent event) implements ResourceReloadListenerRegistrar {
    @Override
    public void register(Identifier id, PreparableReloadListener listener) {
        event.addListener(id, listener);
    }

    @Override
    public void addDependency(Identifier first, Identifier second) {
        event.addDependency(first, second);
    }
}
