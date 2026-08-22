package de.leoxian.moonlightcore.client.model;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface SelectItemModelPropertyRegistrar {
    static void init(String namespace, Consumer<SelectItemModelPropertyRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.selectItemModelProperties(namespace, initializer);
    }

    void register(Identifier identifier, SelectItemModelProperty.Type<?, ?> type);
}
