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
	/// Creates a new item capability, or gets it if it already exists
	/// @param id The id of the capability
	/// @param apiClass The API required type
	/// @param contextClass The context required type
	/// @throws IllegalArgumentException If another `apiClass` or another `contextClass` was already registered with the same id
	static <A, C> ItemCapability<A, C> create(Identifier id, Class<A> apiClass, Class<C> contextClass) {
		return XplatAbstraction.INSTANCE.createItemCapability(id, apiClass, contextClass);
	}

	/// Attempt to retrieve an instance of this capability from the given [ItemStack]
	/// @param stack The [ItemStack]
	/// @param context Additional context for the query
	/// @return The capability instance from the item, or `null` if it couldn't be found
	@Nullable
	A find(ItemStack stack, C context);

	/// Registers an instance provider of this capability for the given item
	/// @param item The item
	/// @param provider The instance provider
	void registerForItem(Supplier<ItemLike> item, ItemCapability.Provider<A, C> provider);

	/// Self-Registers the provider from the item if it implements the API required type
	void registerSelf(Supplier<ItemLike> item);

	/// Registers a fallback provider that can be used on all items if no instance was found when querying
	/// @param provider The fallback provider
	void registerFallbackProvider(ItemCapability.Provider<A, C> provider);

	/// Retrieves the provider used on the given item
	/// @param item The item
	/// @return The instance provider used with the item, or `null` if no provider was found
	ItemCapability.@Nullable Provider<A, C> getProvider(Supplier<Item> item);

	/// @return This capability's id
	Identifier id();

	/// @return This capability's API type
	Class<A> apiClass();

	/// @return This capability's context type
	Class<C> contextClass();

	@FunctionalInterface
	interface Provider<A, C extends @Nullable Object> {
		@Nullable
		A find(ItemStack stack, C context);
	}
}
