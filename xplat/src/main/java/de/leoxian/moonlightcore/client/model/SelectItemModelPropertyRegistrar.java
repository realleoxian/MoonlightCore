package de.leoxian.moonlightcore.client.model;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface SelectItemModelPropertyRegistrar {
    /// Configure and register modded ranged item model properties
    /// @param namespace The mod's id to add this registrar to
    /// @param initializer The initializer of the registrar
    static void init(String namespace, Consumer<SelectItemModelPropertyRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.selectItemModelProperties(namespace, initializer);
    }

    /// Register a new item model property
    /// @param identifier The identifier of the property
    /// @param type The property's type
    void register(Identifier identifier, SelectItemModelProperty.Type<?, ?> type);
}
