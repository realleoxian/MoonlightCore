package de.leoxian.moonlightcore.neoforge.common.pack;

import de.leoxian.moonlightcore.common.pack.ResourceReloadListenerRegistrar;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public record NeoforgeResourceReloadListenerRegistrar(AddServerReloadListenersEvent event) implements ResourceReloadListenerRegistrar {
    @Override
    public void register(Identifier id, Function<HolderLookup.Provider, PreparableReloadListener> func) {
        event.addListener(id, new ContextAwareReloadListener() {
            @Nullable
            private PreparableReloadListener backingListener = null;

            @Override
            public CompletableFuture<Void> reload(SharedState sharedState, Executor executor, PreparationBarrier preparationBarrier, Executor executor1) {
                PreparableReloadListener listener = this.backingListener;
                if (listener == null) {
                    listener = backingListener = func.apply(getRegistryLookup());
                    if (listener == null) {
                        return CompletableFuture.failedFuture(new RuntimeException("Failed to create backing listener"));
                    }
                }
                return listener.reload(sharedState, executor, preparationBarrier, executor1);
            }
        });
    }

    @Override
    public void addDependency(Identifier first, Identifier second) {
        event.addDependency(first, second);
    }
}
