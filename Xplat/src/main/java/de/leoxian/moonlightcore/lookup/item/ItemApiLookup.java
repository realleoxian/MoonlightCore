package de.leoxian.moonlightcore.lookup.item;

import com.mojang.logging.LogUtils;
import de.leoxian.moonlightcore.lookup.ApiLookupMap;
import de.leoxian.moonlightcore.lookup.ApiProviderMap;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ItemApiLookup<A, C> {
    private static final ApiLookupMap<ItemApiLookup<?, ?>> LOOKUPS = ApiLookupMap.create(ItemApiLookup::new);
    private static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("unchecked")
    public static <A, C> ItemApiLookup<A, C> get(ResourceLocation id, Class<A> apiClass, Class<C> contextClass) {
        return (ItemApiLookup<A, C>) LOOKUPS.getLookup(id, apiClass, contextClass);
    }

    private final ApiProviderMap<Item, ItemApiProvider<A, C>> providerMap = ApiProviderMap.create();
    private final List<ItemApiProvider<A, C>> fallbackProviders = new CopyOnWriteArrayList<>();

    private final ResourceLocation id;
    private final Class<A> apiClass;
    private final Class<C> contextClass;

    private ItemApiLookup(ResourceLocation id, Class<A> apiClass, Class<C> contextClass) {
        this.id = id;
        this.apiClass = apiClass;
        this.contextClass = contextClass;
    }

    public @Nullable A find(ItemStack stack, C context) {
        Objects.requireNonNull(stack, "Item stack may not be null");

        ItemApiProvider<A, C> provider = providerMap.get(stack.getItem());

        if(provider != null) {
            A instance = provider.find(stack, context);

            if(instance != null) {
                return instance;
            }
        }

        for(ItemApiProvider<A, C> fallbackProvider : fallbackProviders) {
            A instance = fallbackProvider.find(stack, context);

            if(instance != null) {
                return instance;
            }
        }

        return null;
    }

    public void registerSelf(ItemLike... items) {
        for(ItemLike itemLike : items) {
            Item item = itemLike.asItem();

            if(!apiClass.isAssignableFrom(item.getClass())) {
                String errorMessage = String.format("Failed to register self-implementing items. API class %s is not assignable from item class %s",
                        this.apiClass.getCanonicalName(),
                        item.getClass().getCanonicalName()
                );

                throw new IllegalArgumentException(errorMessage);
            }
        }

        registerForItems((stack, ctx) -> (A) stack.getItem(), items);
    }

    public void registerForItems(ItemApiProvider<A, C> provider, ItemLike... items) {
        Objects.requireNonNull(provider, "ItemApiProvider may not be null");

        if(items.length == 0) {
            throw new IllegalArgumentException("Must register at least one ItemLike instance with an ItemApiProvider");
        }

        for(ItemLike itemLike : items) {
            Item item = itemLike.asItem();
            Objects.requireNonNull(item, "Item like in item form may not be null");

            if(providerMap.putIfAbsent(item, provider) != null) {
                LOGGER.warn("Encountered duplicated API provider registration for item: {}", BuiltInRegistries.ITEM.getId(item));
            }
        }
    }

    public void registerFallback(ItemApiProvider<A, C> fallbackProvider) {
        Objects.requireNonNull(fallbackProvider, "ItemApiProvider may not be null");
        this.fallbackProviders.add(fallbackProvider);
    }

    public @Nullable ItemApiProvider<A, C> getProvider(Item item) {
        return providerMap.get(item);
    }

    public ResourceLocation id() {
        return id;
    }

    public Class<A> apiClass() {
        return apiClass;
    }

    public Class<C> contextClass() {
        return contextClass;
    }

    @FunctionalInterface
    public interface ItemApiProvider<A, C> {
        @Nullable A find(ItemStack itemStack, C context);
    }
}
