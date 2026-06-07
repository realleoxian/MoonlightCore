package de.realleoxian.moonlightcore.api.client.render;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.function.Supplier;

@ApiStatus.NonExtendable
public interface ItemColorHandlerRegistrar {
    void registerItemColor(ItemColor color, Supplier<ItemLike> item);

    default void registerItemsColor(ItemColor color, Supplier<ItemLike>... items) {
        Arrays.stream(items).forEach(item -> registerItemColor(color, item));
    }
}
