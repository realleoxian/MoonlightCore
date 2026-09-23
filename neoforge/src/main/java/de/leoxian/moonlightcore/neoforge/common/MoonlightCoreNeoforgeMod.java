package de.leoxian.moonlightcore.neoforge.common;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import de.leoxian.moonlightcore.internal.common.core.MoonlightCore;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(value = "moonlightcore")
public class MoonlightCoreNeoforgeMod {
	public MoonlightCoreNeoforgeMod(IEventBus eventBus, ModContainer container) {
		XplatAbstraction.INSTANCE.initialize();
		MoonlightCore.initialize();
		ModDeferredRegisters.register("moonlightcore", eventBus);
		ModEventBuses.registerEventBus("moonlightcore", eventBus);

		eventBus.addListener(this::onCommonSetup);
	}

	private void onCommonSetup(final FMLCommonSetupEvent event) {
		XplatAbstraction.INSTANCE.initialize();
	}
}
