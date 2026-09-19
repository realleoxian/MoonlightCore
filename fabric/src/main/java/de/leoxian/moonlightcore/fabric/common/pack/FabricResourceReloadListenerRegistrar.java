package de.leoxian.moonlightcore.fabric.common.pack;

import de.leoxian.moonlightcore.common.pack.ResourceReloadListenerRegistrar;
import net.fabricmc.fabric.api.resource.v1.DataResourceLoader;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.Function;

public enum FabricResourceReloadListenerRegistrar implements ResourceReloadListenerRegistrar {
    INSTANCES
    ;

    @Override
    public void register(Identifier id, Function<HolderLookup.Provider, PreparableReloadListener> listener) {
        DataResourceLoader.get().registerReloadListener(id, listener);
    }

    @Override
    public void addDependency(Identifier first, Identifier second) {
        DataResourceLoader.get().addListenerOrdering(first, second);
    }
}
