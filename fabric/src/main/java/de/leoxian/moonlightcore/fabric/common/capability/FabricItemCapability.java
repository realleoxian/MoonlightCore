package de.leoxian.moonlightcore.fabric.common.capability;

import de.leoxian.moonlightcore.common.capability.item.ItemCapability;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class FabricItemCapability<A, C> implements ItemCapability<A, C> {
    private static final Map<Identifier, FabricItemCapability<?, ?>> CAPABILITIES = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <A, C> ItemCapability<A, C> get(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        FabricItemCapability<?, ?> existing = CAPABILITIES.computeIfAbsent(id, key -> new FabricItemCapability<>(key, apiClass, contextClass));

        if (existing.apiClass() != apiClass) {
            throw new IllegalStateException("Attempted to register capability " + id + " with existing type class " + existing.apiClass() + " != " + apiClass);
        }
        if (existing.contextClass() != contextClass) {
            throw new IllegalStateException("Attempted to register capability " + id + " with existing context class " + existing.contextClass() + " != " + contextClass);
        }

        return (ItemCapability<A, C>) existing;
    }

    private final ItemApiLookup<A, C> apiLookup;

    FabricItemCapability(Identifier id, Class<A> apiCLass, Class<C> contextClass) {
        this.apiLookup = ItemApiLookup.get(id, apiCLass, contextClass);
    }

    @Override
    public @Nullable A find(ItemStack stack, C context) {
        return apiLookup.find(stack, context);
    }

    @Override
    public void registerForItem(Supplier<ItemLike> item, Provider<A, C> provider) {
        apiLookup.registerForItems(provider::find, item.get());
    }

    @Override
    public void registerSelf(Supplier<ItemLike> item) {
        apiLookup.registerSelf(item.get());
    }

    @Override
    public void registerFallbackProvider(Provider<A, C> provider) {
        apiLookup.registerFallback(provider::find);
    }

    @Override
    public @Nullable Provider<A, C> getProvider(Supplier<Item> item) {
        ItemApiLookup.ItemApiProvider<A, C> provider = apiLookup.getProvider(item.get());
        if (provider == null) {
            return null;
        }
        return provider::find;
    }

    @Override
    public Identifier id() {
        return this.apiLookup.getId();
    }

    @Override
    public Class<A> apiClass() {
        return this.apiLookup.apiClass();
    }

    @Override
    public Class<C> contextClass() {
        return this.apiLookup.contextClass();
    }
}
