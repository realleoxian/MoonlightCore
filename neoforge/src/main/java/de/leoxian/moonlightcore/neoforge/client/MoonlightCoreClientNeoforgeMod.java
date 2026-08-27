package de.leoxian.moonlightcore.neoforge.client;

import de.leoxian.moonlightcore.common.ClientModEntrypoint;
import de.leoxian.moonlightcore.internal.common.mod.client.InternalClientMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = "moonlightcore", dist = Dist.CLIENT)
public class MoonlightCoreClientNeoforgeMod {
    public MoonlightCoreClientNeoforgeMod(IEventBus eventBus, ModContainer container) {
        ClientModEntrypoint.init("moonlightcore", InternalClientMod::initializeClientMod);
    }
}
