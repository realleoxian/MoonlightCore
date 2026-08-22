package de.leoxian.moonlightcore.client.pack;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface ClientResourceReloadListenerRegistrar {
    void register(Identifier id, PreparableReloadListener listener);

    void addDependency(Identifier first, Identifier second);
}
