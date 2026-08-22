package de.leoxian.moonlightcore.neoforge.client.model;

import com.mojang.serialization.MapCodec;
import de.leoxian.moonlightcore.client.model.RangeSelectItemModelPropertyRegistrar;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;

public record NeoforgeRangeSelectItemModelPropertyRegistrar(RegisterRangeSelectItemModelPropertyEvent event) implements RangeSelectItemModelPropertyRegistrar {
    @Override
    public void register(Identifier id, MapCodec<? extends RangeSelectItemModelProperty> source) {
        event.register(id, source);
    }
}
