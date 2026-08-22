package de.leoxian.moonlightcore.neoforge.client.model;

import de.leoxian.moonlightcore.client.model.SelectItemModelPropertyRegistrar;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterSelectItemModelPropertyEvent;

public record NeoforgeSelectItemModelPropertyRegistrar(RegisterSelectItemModelPropertyEvent event) implements SelectItemModelPropertyRegistrar {
    @Override
    public void register(Identifier identifier, SelectItemModelProperty.Type<?, ?> type) {
        event.register(identifier, type);
    }
}
