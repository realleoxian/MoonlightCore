package de.leoxian.moonlightcore.common.pack;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface ResourceReloadListenerRegistrar {
    void register(Identifier id, PreparableReloadListener listener);

    void addDependency(Identifier first, Identifier second);
}
