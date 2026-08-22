package de.leoxian.moonlightcore.neoforge.client.pack;

import de.leoxian.moonlightcore.client.pack.ClientResourceReloadListenerRegistrar;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

public record NeoforgeClientResourceReloadListenerRegistrar(AddClientReloadListenersEvent event) implements ClientResourceReloadListenerRegistrar {
    @Override
    public void register(Identifier id, PreparableReloadListener listener) {
        event.addListener(id, listener);
    }

    @Override
    public void addDependency(Identifier first, Identifier second) {
        event.addDependency(first, second);
    }
}
