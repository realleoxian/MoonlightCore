package de.realleoxian.moonlightcore.api.capability;

import de.realleoxian.moonlightcore.api.MoonlightCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface ItemCapabilities {
    static ItemCapabilities get() {
        return MoonlightCore.ABSTRACTION.getItemCapabilities();
    }

    @Nullable
    <T, C> T find(CapabilityType<Item, T, C> capabilityType, ItemStack stack);

    <T, C> CapabilityType<Item, T, C> create(ResourceLocation id, Class<T> capabilityType, Class<C> contextType);

    <T, C> void registerForItems(CapabilityType<Item, T, C> capabilityType, Provider<T, C> provider, Supplier<ItemLike>... items);

    <T, C> void registerFallback(CapabilityType<Item, T, C> capabilityType, Provider<T, C> provider);

    <T, C> Provider<T, C> getProvider(CapabilityType<Item, T, C> capabilityType, Supplier<ItemLike> item);

    interface Provider<T, C> {
        @Nullable
        T find(ItemStack stack, C context);
    }
}
