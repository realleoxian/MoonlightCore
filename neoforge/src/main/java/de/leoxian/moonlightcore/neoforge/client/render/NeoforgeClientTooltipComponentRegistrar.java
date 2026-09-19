package de.leoxian.moonlightcore.neoforge.client.render;

import de.leoxian.moonlightcore.client.render.ClientTooltipComponentRegistrar;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

import java.util.function.Function;

public record NeoforgeClientTooltipComponentRegistrar(RegisterClientTooltipComponentFactoriesEvent event) implements ClientTooltipComponentRegistrar {
    @Override
    public <T extends TooltipComponent> void register(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory) {
        event.register(type, factory);
    }
}
