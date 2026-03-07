package de.leoxian.moonlightcore.api.runtime;

import java.util.ServiceLoader;

public interface MoonlightCoreRuntimeFactory {

    static MoonlightCoreRuntimeFactory createFactory() {
        return ServiceLoader.load(MoonlightCoreRuntimeFactory.class).findFirst().orElseThrow();
    }

    MoonlightCoreRuntime<ModLoadingRuntimeContext> make();

}
