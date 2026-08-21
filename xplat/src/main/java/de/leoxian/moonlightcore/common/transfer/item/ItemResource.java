package de.leoxian.moonlightcore.common.transfer.item;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.common.transfer.resource.RegisteredResourceWithData;
import de.leoxian.moonlightcore.internal.common.transfer.item.ItemResourceImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
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

    static ItemResource of(Item item, DataComponentPatch componentPatch) {
        return ItemResourceImpl.of(item, componentPatch);
    }

    static ItemResource of(ItemLike item, DataComponentPatch componentPatch) {
        return of(item.asItem(), componentPatch);
    }

    static ItemResource of(Holder<Item> holder, DataComponentPatch componentPatch) {
        return of(holder.value(), componentPatch);
    }

    static ItemResource of(Item item) {
        return ItemResourceImpl.of(item, DataComponentPatch.EMPTY);
    }

    static ItemResource of(ItemLike item) {
        return of(item.asItem(), DataComponentPatch.EMPTY);
    }

    static ItemResource of(Holder<Item> holder) {
        return of(holder.value(), DataComponentPatch.EMPTY);
    }

    static ItemResource fromStack(ItemStack stack) {
        return of(stack.getItem(), stack.getComponentsPatch());
    }

    @Override
    ItemResource applyPatch(DataComponentPatch patch);

    Item item();

    ItemStack toStack();

    default ItemStack toStack(int count) {
        return toStack().copyWithCount(count);
    }
}
