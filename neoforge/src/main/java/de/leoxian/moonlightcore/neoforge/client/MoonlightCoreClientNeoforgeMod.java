package de.leoxian.moonlightcore.neoforge.client;

import de.leoxian.moonlightcore.common.entrypoint.ClientModInitializer;
import de.leoxian.moonlightcore.internal.common.mod.client.InternalClientMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = "moonlightcore", dist = Dist.CLIENT)
public class MoonlightCoreClientNeoforgeMod {
    public MoonlightCoreClientNeoforgeMod(IEventBus eventBus, ModContainer container) {
        ClientModInitializer.initialize("moonlightcore", InternalClientMod.class);
    }
}
