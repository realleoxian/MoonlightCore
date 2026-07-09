package de.leoxian.moonlightcore.client.model;

import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface SelectItemModelPropertyRegistrar {
    void register(Identifier identifier, SelectItemModelProperty.Type<?, ?> type);
}
