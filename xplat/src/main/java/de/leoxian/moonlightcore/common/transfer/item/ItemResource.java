package de.leoxian.moonlightcore.common.transfer.item;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.common.transfer.resource.RegisteredResourceWithData;
import de.leoxian.moonlightcore.internal.common.transfer.item.ItemResourceImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ItemResource extends RegisteredResourceWithData<Item> {
	ItemResource EMPTY = ItemResourceImpl.EMPTY;
	Codec<ItemResource> CODEC = ItemResourceImpl.CODEC;
	StreamCodec<RegistryFriendlyByteBuf, ItemResource> STREAM_CODEC = ItemResourceImpl.STREAM_CODEC;

	/// Creates a new item resource from the given item and components patch
	/// @param item The item
	/// @param componentPatch The components data
	static ItemResource of(Item item, DataComponentPatch componentPatch) {
		return ItemResourceImpl.of(item, componentPatch);
	}

	/// Creates a new item resource from the given item-like object and components patch
	/// @param item The item-like object
	/// @param componentPatch The components data
	static ItemResource of(ItemLike item, DataComponentPatch componentPatch) {
		return of(item.asItem(), componentPatch);
	}

	/// Creates a new item resource from the given item holder and components patch
	/// @param holder the item holder
	/// @param componentPatch The components data
	static ItemResource of(Holder<Item> holder, DataComponentPatch componentPatch) {
		return of(holder.value(), componentPatch);
	}

	/// Creates a new item resource from the given item
	/// @param item The item
	static ItemResource of(Item item) {
		return ItemResourceImpl.of(item, DataComponentPatch.EMPTY);
	}

	/// Creates a new item resource from the given item-like object
	/// @param item The item
	static ItemResource of(ItemLike item) {
		return of(item.asItem(), DataComponentPatch.EMPTY);
	}

	/// Creates a new item resource from the given item holder
	/// @param holder The item holder
	static ItemResource of(Holder<Item> holder) {
		return of(holder.value(), DataComponentPatch.EMPTY);
	}

	/// Creates a new item resource from and [ItemStack]
	/// @param stack The item stack
	static ItemResource fromStack(ItemStack stack) {
		return of(stack.getItem(), stack.getComponentsPatch());
	}

	@Override
	ItemResource applyPatch(DataComponentPatch patch);

	/// @return The item of this resource
	Item item();

	/// @return An item stack from this resource's item and data
	ItemStack toStack();

	/// @param count The count of the stack
	/// @return An item stack from this resource's item and data with the given count
	default ItemStack toStack(int count) {
		return toStack().copyWithCount(count);
	}
}
