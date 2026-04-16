package de.realleoxian.moonlightcore.forge.runtime;

import de.realleoxian.moonlightcore.api.runtime.ModLoadingRuntimeContext;
import net.minecraftforge.eventbus.api.IEventBus;

public record ForgeModLoadingContext(IEventBus eventBus) implements ModLoadingRuntimeContext {

}
