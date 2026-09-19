package de.leoxian.moonlightcore.fabric.client.model;

import de.leoxian.moonlightcore.client.model.SelectItemModelPropertyRegistrar;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.Identifier;

public enum FabricSelectItemModelPropertyRegistrar implements SelectItemModelPropertyRegistrar {
    INSTANCE
    ;

    @Override
    public void register(Identifier identifier, SelectItemModelProperty.Type<?, ?> type) {
        SelectItemModelProperties.ID_MAPPER.put(identifier, type);
    }
}
