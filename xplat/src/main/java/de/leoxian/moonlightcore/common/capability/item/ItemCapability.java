package de.leoxian.moonlightcore.common.capability.item;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

@ApiStatus.NonExtendable
public interface ItemCapability<A, C extends @Nullable Object> {
    static <A, C> ItemCapability<A, C> get(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        return XplatAbstraction.INSTANCE.getItemCapability(id, apiClass, contextClass);
    }

    @Nullable
    A find(ItemStack stack, C context);

    void registerForItem(Supplier<ItemLike> item, ItemCapability.Provider<A, C> provider);

    void registerSelf(Supplier<ItemLike> item);

    void registerFallbackProvider(ItemCapability.Provider<A, C> provider);

    ItemCapability.@Nullable Provider<A, C> getProvider(Supplier<Item> item);

    Identifier id();

    Class<A> apiClass();

    Class<C> contextClass();

    @FunctionalInterface
    interface Provider<A, C extends @Nullable Object> {
        @Nullable
        A find(ItemStack stack, C context);
    }
}
