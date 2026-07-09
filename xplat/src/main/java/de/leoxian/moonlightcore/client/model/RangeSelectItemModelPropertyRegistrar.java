package de.leoxian.moonlightcore.client.model;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface RangeSelectItemModelPropertyRegistrar {
    void register(Identifier id, MapCodec<? extends RangeSelectItemModelProperty> source);
}
