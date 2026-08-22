package de.leoxian.moonlightcore.client.pack;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.Consumer;

public interface ClientResourceReloadListenerRegistrar {
    static void init(String namespace, Consumer<ClientResourceReloadListenerRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.resourceReloadListeners(namespace, initializer);
    }

    void register(Identifier id, PreparableReloadListener listener);

    void addDependency(Identifier first, Identifier second);
}
