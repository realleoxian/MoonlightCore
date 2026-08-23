package de.leoxian.moonlightcore.fabric.common;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import de.leoxian.moonlightcore.internal.common.mod.InternalMod;
import de.leoxian.moonlightcore.internal.common.mod.client.InternalClientMod;
import net.fabricmc.api.ModInitializer;

public class MoonlightCoreFabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        XplatAbstraction.INSTANCE.initialize();
        XplatAbstraction.INSTANCE.initializeMod("moonlightcore", InternalClientMod.class);
    }
}
