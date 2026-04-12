package de.realleoxian.moonlightcore.api.client.runtime;

import de.realleoxian.moonlightcore.api.runtime.ModLoadingRuntimeContext;

import java.util.ServiceLoader;

public interface MoonlightCoreClientRuntimeFactory {
    static MoonlightCoreClientRuntimeFactory createFactory() {
        return ServiceLoader.load(MoonlightCoreClientRuntimeFactory.class).findFirst().orElseThrow();
    }

    MoonlightCoreClientRuntime<ModLoadingRuntimeContext> make();
}
