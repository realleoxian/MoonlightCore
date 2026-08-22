package de.leoxian.moonlightcore.neoforge.common;

import net.neoforged.bus.api.IEventBus;

@FunctionalInterface
public interface ModEventBusRegistrable {
    void register(IEventBus modEventBus);
}
