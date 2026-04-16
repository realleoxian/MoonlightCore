package de.realleoxian.moonlightcore.forge.client.runtime;

import de.realleoxian.moonlightcore.api.client.runtime.MoonlightCoreClientRuntime;
import de.realleoxian.moonlightcore.api.client.runtime.MoonlightCoreClientRuntimeFactory;

public class ForgeMoonlightCoreClientRuntimeFactory implements MoonlightCoreClientRuntimeFactory {
    @Override
    public MoonlightCoreClientRuntime<?> make() {
        return new ForgeMoonlightCoreClientRuntime();
    }
}
