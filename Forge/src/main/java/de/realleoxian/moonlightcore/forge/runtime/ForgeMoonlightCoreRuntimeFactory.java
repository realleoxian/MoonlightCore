package de.realleoxian.moonlightcore.forge.runtime;

import de.realleoxian.moonlightcore.api.runtime.MoonlightCoreRuntime;
import de.realleoxian.moonlightcore.api.runtime.MoonlightCoreRuntimeFactory;

public final class ForgeMoonlightCoreRuntimeFactory implements MoonlightCoreRuntimeFactory {
    @Override
    public MoonlightCoreRuntime<?> make() {
        return new ForgeMoonlightCoreRuntime();
    }
}
