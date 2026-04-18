package de.realleoxian.moonlightcore.fabric.runtime;

import de.realleoxian.moonlightcore.api.runtime.MoonlightCoreRuntime;
import de.realleoxian.moonlightcore.api.runtime.MoonlightCoreRuntimeFactory;

public class FabricMoonlightCoreRuntimeFactory implements MoonlightCoreRuntimeFactory {
    @Override
    public MoonlightCoreRuntime<?> make() {
        return new FabricMoonlightCoreRuntime();
    }
}
