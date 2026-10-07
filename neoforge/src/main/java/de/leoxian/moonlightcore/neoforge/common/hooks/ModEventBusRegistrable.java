package de.leoxian.moonlightcore.neoforge.common.hooks;

import net.neoforged.bus.api.IEventBus;

@FunctionalInterface
public interface ModEventBusRegistrable {
	void register(IEventBus modEventBus);
}
