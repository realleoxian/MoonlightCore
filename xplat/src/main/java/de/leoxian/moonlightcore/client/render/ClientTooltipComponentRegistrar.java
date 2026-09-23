package de.leoxian.moonlightcore.client.render;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ClientTooltipComponentRegistrar {
	/// Configure and register client tooltip components
	/// @param namespace The mod's id
	/// @param initializer The initializer
	static void configure(String namespace, Consumer<ClientTooltipComponentRegistrar> initializer) {
		XplatClientAbstraction.INSTANCE.clientTooltips(namespace, initializer);
	}

	/// Register a client tooltip component to render for a tooltip component data
	/// @param type The tooltip component type
	/// @param factory The factory method of the tooltip renderer
	<T extends TooltipComponent> void register(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory);
}
