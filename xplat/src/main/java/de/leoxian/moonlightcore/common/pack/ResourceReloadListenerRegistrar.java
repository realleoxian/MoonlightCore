package de.leoxian.moonlightcore.common.pack;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.Consumer;

public interface ResourceReloadListenerRegistrar {
    static void init(Consumer<ResourceReloadListenerRegistrar> initializer) {
        XplatAbstraction.INSTANCE.serverReloadListeners(initializer);
    }

    void register(Identifier id, PreparableReloadListener listener);

    void addDependency(Identifier first, Identifier second);
}
