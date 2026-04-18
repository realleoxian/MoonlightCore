package de.realleoxian.moonlightcore.fabric.client.runtime;

import de.realleoxian.moonlightcore.api.client.runtime.MoonlightCoreClientRuntime;
import de.realleoxian.moonlightcore.api.client.runtime.MoonlightCoreClientRuntimeFactory;

public class FabricMoonlightCoreClientRuntimeFactory implements MoonlightCoreClientRuntimeFactory {
    @Override
    public MoonlightCoreClientRuntime<?> make() {
        return new FabricMoonlightCoreClientRuntime();
    }
}
