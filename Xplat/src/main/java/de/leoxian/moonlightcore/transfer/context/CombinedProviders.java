package de.leoxian.moonlightcore.transfer.context;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import de.leoxian.moonlightcore.lookup.item.ItemApiLookup;
import de.leoxian.moonlightcore.transfer.CombinedStorage;
import de.leoxian.moonlightcore.transfer.Storage;
import de.leoxian.moonlightcore.transfer.fluid.FluidResource;
import de.leoxian.moonlightcore.transfer.fluid.FluidStorage;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CombinedProviders {
    public static Event<FluidStorage.CombinedItemApiProvider> createEvent(boolean invokeFallback) {
        return EventFactory.of(listeners -> context -> {
            List<Storage<FluidResource>> storages = new ArrayList<>();

            for(FluidStorage.CombinedItemApiProvider listener : listeners) {
                Storage<FluidResource> found = listener.find(context);

                if(found != null) {
                    storages.add(found);
                }
            }

            if(!storages.isEmpty() && invokeFallback) {
                Storage<FluidResource> fallbackFOound = FluidStorage.GENERAL_COMBINED_PROVIDERS_EVENT.invoker().find(context);

                if(fallbackFOound != null) {
                    storages.add(fallbackFOound);
                }
            }

            return storages.isEmpty() ? null : new CombinedStorage<>(storages);
        });
    }

    private static class Provider implements ItemApiLookup.ItemApiProvider<Storage<FluidResource>, ItemStorageContext> {
        private final Event<FluidStorage.CombinedItemApiProvider> event = createEvent(true);

        @Override
        public @Nullable Storage<FluidResource> find(ItemStack itemStack, ItemStorageContext context) {
            if(!context.resource().fullyMatches(itemStack.getItem(), itemStack.getTag())) {
                String errorMessage = String.format(
                        "Query stack %s as ItemStorageContext resource %s don't match",
                        itemStack,
                        context.resource()
                );

                throw new IllegalArgumentException(errorMessage);
            }

            return event.invoker().find(context);
        }
    }

    public static Event<FluidStorage.CombinedItemApiProvider> getOrCreateItemEvent(Item item) {
        ItemApiLookup.ItemApiProvider<Storage<FluidResource>, ItemStorageContext> existingProvider = FluidStorage.ITEM.getProvider(item);

        if(existingProvider == null) {
            FluidStorage.ITEM.registerForItems(new Provider(), item);
            existingProvider = FluidStorage.ITEM.getProvider(item);
        }

        if(existingProvider instanceof Provider registered) {
            return registered.event;
        } else {
            String errorMessage = String.format(
                    "An incompatible provider was already registered for item %s. Provider: %s",
                    item,
                    existingProvider
            );

            throw new IllegalStateException(errorMessage);
        }
    }
}
