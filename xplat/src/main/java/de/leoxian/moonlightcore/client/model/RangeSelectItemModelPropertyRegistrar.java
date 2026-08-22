package de.leoxian.moonlightcore.client.model;

import com.mojang.serialization.MapCodec;
import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface RangeSelectItemModelPropertyRegistrar {
    static void init(String namespace, Consumer<RangeSelectItemModelPropertyRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.rangeSelectItemModelProperties(namespace, initializer);
    }

    void register(Identifier id, MapCodec<? extends RangeSelectItemModelProperty> source);
}
