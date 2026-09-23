package de.leoxian.moonlightcore.neoforge.client;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import de.leoxian.moonlightcore.internal.common.core.client.MoonlightCoreClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = "moonlightcore", dist = Dist.CLIENT)
public class MoonlightCoreClientNeoforgeMod {
    public MoonlightCoreClientNeoforgeMod(IEventBus eventBus, ModContainer container) {
        XplatClientAbstraction.INSTANCE.initialize();
        MoonlightCoreClient.initializeClientMod();
    }
}
