package de.leoxian.moonlightcore.api.apilookup.item;

import de.leoxian.moonlightcore.api.apilookup.ApiLookup;
import de.leoxian.moonlightcore.impl.apilookup.ItemApiLookupImpl;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public interface ItemApiLookup<A, C extends @Nullable Object> extends ApiLookup<A, C> {
    static <A, C extends @Nullable Object> ItemApiLookup<A, C> find(ResourceLocation name, Class<A> apiClass, Class<C> contextClass) {
        return ItemApiLookupImpl.find(name, apiClass, contextClass);
    }

    @Nullable
    A find(ItemStack itemStack, C context);

    void register(ItemApiLookup.Provider<A, C> provider, ItemLike... items);

    void registerFallback(ItemApiLookup.Provider<A, C> provider);

    ItemApiLookup.@Nullable Provider<A, C> getProvider(ItemLike itemLike);

    List<Provider<A, C>> getFallbackProviders();

    @FunctionalInterface
    interface Provider<A, C extends @Nullable Object> {
        @Nullable
        A get(ItemStack itemStack, C context);
    }
}

