package de.leoxian.moonlightcore.client.model;

import com.mojang.serialization.MapCodec;
import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface RangeSelectItemModelPropertyRegistrar {
    /// Configure and register modded item model properties
    /// @param namespace The mod's id to add this registrar to
    /// @param initializer The initializer of the registrar
    static void init(String namespace, Consumer<RangeSelectItemModelPropertyRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.rangeSelectItemModelProperties(namespace, initializer);
    }

    /// Register a new range select item model property
    /// @param id The identifier of the property
    /// @param source The source codec of the property
    void register(Identifier id, MapCodec<? extends RangeSelectItemModelProperty> source);
}
