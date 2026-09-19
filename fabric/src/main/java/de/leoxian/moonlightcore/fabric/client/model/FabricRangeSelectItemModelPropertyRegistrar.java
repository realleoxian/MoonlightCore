package de.leoxian.moonlightcore.fabric.client.model;

import com.mojang.serialization.MapCodec;
import de.leoxian.moonlightcore.client.model.RangeSelectItemModelPropertyRegistrar;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;

public enum FabricRangeSelectItemModelPropertyRegistrar implements RangeSelectItemModelPropertyRegistrar {
    INSTANCE
    ;

    @Override
    public void register(Identifier id, MapCodec<? extends RangeSelectItemModelProperty> source) {
        RangeSelectItemModelProperties.ID_MAPPER.put(id, source);
    }
}
