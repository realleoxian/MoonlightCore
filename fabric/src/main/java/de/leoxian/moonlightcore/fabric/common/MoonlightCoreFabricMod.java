package de.leoxian.moonlightcore.fabric.common;

import de.leoxian.moonlightcore.internal.common.mod.InternalMod;
import net.fabricmc.api.ModInitializer;

public class MoonlightCoreFabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        de.leoxian.moonlightcore.common.entrypoint.ModInitializer.initializeMod("moonlightcore", InternalMod.class);
    }
}
