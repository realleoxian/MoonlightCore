package de.leoxian.moonlightcore.fabric;

import de.leoxian.moonlightcore.core.MoonlightCore;
import net.fabricmc.api.ModInitializer;

public final class MoonlightCoreFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        MoonlightCore.init();
    }

}
