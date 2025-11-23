package de.leoxian.moonlightcore.transfer.fluid;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import de.leoxian.moonlightcore.lookup.item.ItemApiLookup;
import de.leoxian.moonlightcore.transfer.CombinedStorage;
import de.leoxian.moonlightcore.transfer.Storage;
import de.leoxian.moonlightcore.transfer.context.ContainerItemContext;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
class CombinedProviders {
    static Event<FluidStorage.CombinedItemApiProvider> createEvent(boolean invokeFallback) {
        return EventFactory.of(listeners -> context -> {
            List<Storage<FluidResource>> storages = new ArrayList<>();

            for(FluidStorage.CombinedItemApiProvider listener : listeners) {
                Storage<FluidResource> storage = listener.find(context);

                if(storage != null) {
                    storages.add(storage);
                }
            }

            if(!storages.isEmpty() && invokeFallback) {
                Storage<FluidResource> fallback = FluidStorage.GENERAL_COMBINED_PROVIDER.invoker().find(context);

                if(fallback != null) {
                    storages.add(fallback);
                }
            }

            return storages.isEmpty() ? null : new CombinedStorage<>(storages);
        });
    }

    public static Event<FluidStorage.CombinedItemApiProvider> getOrCreateItemEvent(Item item) {
        ItemApiLookup.ItemApiProvider<Storage<FluidResource>, ContainerItemContext> existingProvider = FluidStorage.ITEM.getProvider(item);

        if(existingProvider == null) {
            FluidStorage.ITEM.registerForItems(new Provider(), item);
            existingProvider = FluidStorage.ITEM.getProvider(item);
        }

        if(existingProvider instanceof Provider registeredProvider) {
            return registeredProvider.event;
        } else {
            String errorMessage = String.format(
                    "An incompatible provider was already registered for item %s. Provider: %s",
                    item,
                    existingProvider
            );

            throw new IllegalStateException(errorMessage);
        }
    }

    private static class Provider implements ItemApiLookup.ItemApiProvider<Storage<FluidResource>, ContainerItemContext> {
        private final Event<FluidStorage.CombinedItemApiProvider> event = createEvent(true);

        @Override
        public @Nullable Storage<FluidResource> find(ItemStack itemStack, ContainerItemContext context) {
            if(!context.getResource().matches(itemStack)) {
                String errorMessage = String.format(
                        "Query stack %s and ContainerItemContext resource %s don't match",
                        itemStack,
                        context.getResource()
                );

                throw new IllegalArgumentException(errorMessage);
            }

            return event.invoker().find(context);
        }
    }
}
