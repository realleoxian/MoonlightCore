package de.realleoxian.moonlightcore.api.client;

import de.realleoxian.moonlightcore.api.ModLoadContext;
import de.realleoxian.moonlightcore.api.client.runtime.ClientXplatAbstraction;
import de.realleoxian.moonlightcore.api.client.runtime.ClientXplatAbstractionFactory;

import java.util.ServiceLoader;

public final class MoonlightCoreClient {
    public static final ClientXplatAbstraction<?> ABSTRACTION = create();

    @SuppressWarnings("unchecked")
    private static ClientXplatAbstraction<ModLoadContext> create() {
        var loader = ServiceLoader.load(ClientXplatAbstractionFactory.class);
        var factory = loader.findFirst().orElseThrow();
        return (ClientXplatAbstraction<ModLoadContext>) factory.make();
    }

    private MoonlightCoreClient() {}
}
