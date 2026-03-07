package de.leoxian.moonlightcore.impl.apilookup;

import de.leoxian.moonlightcore.api.apilookup.ApiLookupRegistry;
import de.leoxian.moonlightcore.api.apilookup.ItemApiLookup;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public final class ItemApiLookupImpl<A, C extends @Nullable Object> extends ApiLookupImpl<A, C> implements ItemApiLookup<A, C> {
    private static final ApiLookupRegistry<ItemApiLookup<?, ?>> REGISTRY = ApiLookupRegistryImpl.create(ItemApiLookupImpl::new);

    @SuppressWarnings("unchecked")
    public static <A, C extends @Nullable Object> ItemApiLookup<A, C> get(ResourceLocation name, Class<A> apiClass, Class<C> contextClass) {
        return (ItemApiLookup<A, C>) REGISTRY.create(name, apiClass, contextClass);
    }

    private final Map<Item, ItemApiLookup.Provider<A, C>> providers = new IdentityHashMap<>();

    private ItemApiLookupImpl(ResourceLocation name, Class<A> apiClass, Class<C> contextClass) {
        super(name, apiClass, contextClass);
    }

    @Override
    public @Nullable A get(ItemStack itemStack, C context) {
        if(itemStack.isEmpty()) {
            return null;
        }

        Item item = itemStack.getItem();
        ItemApiLookup.@Nullable Provider<A, C> provider = getProvider(item);
        if(provider == null) {
            return null;
        }

        return provider.get(itemStack, context);
    }

    @Override
    public void register(ItemApiLookup.Provider<A, C> provider, ItemLike... items) {
        Objects.requireNonNull(provider, "Item API provider cannot be 'null'");

        if(items.length == 0) {
            throw new IllegalArgumentException("Must register at least one ItemLike instance with an ItemApiLookup$Provider");
        } else {
            for(ItemLike itemLike : items) {
                Item item = itemLike.asItem();
                Objects.requireNonNull(item, "ItemLike as item cannot be 'null'");

                if(providers.putIfAbsent(item, provider) != null) {
                    throw new IllegalStateException("Duplicated API definition encountered for item '" + BuiltInRegistries.ITEM.getKey(item) + "'");
                }
            }
        }
    }

    @Override
    public @Nullable Provider<A, C> getProvider(ItemLike itemLike) {
        Objects.requireNonNull(itemLike, "ItemLike cannot be 'null'");

        Item item = itemLike.asItem();
        Objects.requireNonNull(item, "ItemLike as item cannot be 'null'");
        return providers.get(item);
    }
}
