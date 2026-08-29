package de.leoxian.moonlightcore.common.pack;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ResourceReloadListenerRegistrar {
    static void init(Consumer<ResourceReloadListenerRegistrar> initializer) {
        XplatAbstraction.INSTANCE.serverReloadListeners(initializer);
    }

    void register(Identifier id, Function<HolderLookup.Provider, PreparableReloadListener> listener);

    void addDependency(Identifier first, Identifier second);
}
