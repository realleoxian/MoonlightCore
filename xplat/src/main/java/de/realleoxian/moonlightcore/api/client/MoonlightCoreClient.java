package de.realleoxian.moonlightcore.api.client;

import de.realleoxian.moonlightcore.api.ModLoadContext;
import de.realleoxian.moonlightcore.api.client.internal.ClientXplatAbstraction;
import de.realleoxian.moonlightcore.api.client.internal.ClientXplatAbstractionFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.ServiceLoader;
import java.util.function.Consumer;

public final class MoonlightCoreClient {
    public static final ClientXplatAbstraction<ModLoadContext> ABSTRACTION = create();

    public static void initializeClientMod(String modId, ModLoadContext loadContext, Consumer<ClientModContainer> initializer) {
        ABSTRACTION.initializeClientMod(modId, loadContext, initializer);
    }

    public static void registerPreparableReloadListener(ResourceLocation name, PreparableReloadListener listener) {
        ABSTRACTION.registerPreparableReloadListener(name, listener);
    }

    @SuppressWarnings("unchecked")
    private static ClientXplatAbstraction<ModLoadContext> create() {
        var loader = ServiceLoader.load(ClientXplatAbstractionFactory.class);
        var factory = loader.findFirst().orElseThrow();
        return (ClientXplatAbstraction<ModLoadContext>) factory.make();
    }

    private MoonlightCoreClient() {}
}
