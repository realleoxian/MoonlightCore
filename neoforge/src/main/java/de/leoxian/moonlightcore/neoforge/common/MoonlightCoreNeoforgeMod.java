package de.leoxian.moonlightcore.neoforge.common;

import de.leoxian.moonlightcore.common.entrypoint.ModInitializer;
import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import de.leoxian.moonlightcore.internal.common.mod.InternalMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(value = "moonlightcore")
public class MoonlightCoreNeoforgeMod {
    public MoonlightCoreNeoforgeMod(IEventBus eventBus, ModContainer container) {
        ModDeferredRegisters.register("moonlightcore", eventBus);
        ModEventBuses.registerEventBus("moonlightcore", eventBus);
        ModInitializer.initializeMod("moonlightcore", InternalMod.class);

        eventBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        XplatAbstraction.INSTANCE.initialize();
    }
}
