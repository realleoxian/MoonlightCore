package de.leoxian.moonlightcore.api.apilookup;

import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import de.leoxian.moonlightcore.impl.apilookup.ItemApiLookupImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public interface ItemApiLookup<A, C extends @Nullable Object> extends ApiLookup<A, C> {

    static <A, C extends @Nullable Object> ItemApiLookup<A, C> get(ResourceLocation name, Class<A> apiClass, Class<C> contextClass) {
        return ItemApiLookupImpl.get(name, apiClass, contextClass);
    }

    @Nullable
    A get(ItemStack itemStack, C context);

    void register(ItemApiLookup.Provider<A, C> provider, ItemLike... items);

    void registerFallback(ItemApiLookup.Provider<A, C> provider);

    ItemApiLookup.@Nullable Provider<A, C> getProvider(ItemLike itemLike);

    List<ItemApiLookup.Provider<A, C>> getFallbackProviders();

    @FunctionalInterface
    interface Provider<A, C extends @Nullable Object> {

        @Nullable
        A get(ItemStack itemStack, C context);

    }

}
