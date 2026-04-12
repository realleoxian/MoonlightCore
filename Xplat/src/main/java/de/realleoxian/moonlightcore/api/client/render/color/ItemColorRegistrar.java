package de.realleoxian.moonlightcore.api.client.render.color;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.function.Supplier;

public interface ItemColorRegistrar {
    void registerItemColor(ItemColor color, Supplier<ItemLike> item);

    default void registerItemsColor(ItemColor color, Supplier<ItemLike>... items) {
        Arrays.stream(items).forEach(item -> registerItemColor(color, item));
    }
}
