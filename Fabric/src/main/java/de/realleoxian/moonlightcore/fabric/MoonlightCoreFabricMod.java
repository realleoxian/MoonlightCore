package de.realleoxian.moonlightcore.fabric;

import de.realleoxian.moonlightcore.api.MoonlightCore;
import de.realleoxian.moonlightcore.fabric.core.FabricCoreMod;
import de.realleoxian.moonlightcore.fabric.runtime.EmptyModLoadingRuntimeContext;
import de.realleoxian.moonlightcore.impl.runtime.XplatMoonlightCoreRuntime;
import net.fabricmc.api.ModInitializer;

public class MoonlightCoreFabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        MoonlightCore.initializeMod("moonlightcore", EmptyModLoadingRuntimeContext.INSTANCE, FabricCoreMod::initialize);
        ((XplatMoonlightCoreRuntime<?>) MoonlightCore.getRuntime()).initializeRuntime();
    }
}
