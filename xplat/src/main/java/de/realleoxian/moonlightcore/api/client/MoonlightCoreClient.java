package de.realleoxian.moonlightcore.api.client;

import de.realleoxian.moonlightcore.api.ModLoadContext;
import de.realleoxian.moonlightcore.api.client.runtime.MoonlightCoreClientRuntime;
import de.realleoxian.moonlightcore.api.client.runtime.MoonlightCoreClientRuntimeFactory;

import java.util.ServiceLoader;

public final class MoonlightCoreClient {
    public static final MoonlightCoreClientRuntime<?> RUNTIME = create();

    @SuppressWarnings("unchecked")
    private static MoonlightCoreClientRuntime<ModLoadContext> create() {
        var loader = ServiceLoader.load(MoonlightCoreClientRuntimeFactory.class);
        var factory = loader.findFirst().orElseThrow();
        return (MoonlightCoreClientRuntime<ModLoadContext>) factory.make();
    }

    private MoonlightCoreClient() {}
}
