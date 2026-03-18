package de.leoxian.moonlightcore.api.transfer.item;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.api.transfer.Resource;
import de.leoxian.moonlightcore.impl.transfer.item.ItemResourceImpl;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public interface ItemResource extends Resource<Item> {
    Codec<ItemResource> CODEC = ItemResourceImpl.CODEC;

    static ItemResource blank() {
        return ItemResourceImpl.blank();
    }

    static ItemResource of(Item item, @Nullable CompoundTag tag) {
        return ItemResourceImpl.of(item, tag);
    }

    static ItemResource of(ItemLike itemLike, @Nullable CompoundTag tag) {
        return of(itemLike.asItem(), tag);
    }

    static ItemResource of(Item item) {
        return of(item, null);
    }

    static ItemResource of(ItemLike itemLike) {
        return of(itemLike, null);
    }

    static ItemResource fromItemStack(ItemStack stack) {
        return of(stack.getItem(), stack.getTag());
    }

    static ItemResource fromBuffer(FriendlyByteBuf byteBuf) {
        return ItemResourceImpl.fromBuffer(byteBuf);
    }

    default boolean matches(ItemStack stack) {
        return is(stack.getItem()) && tagMatches(stack.getTag());
    }

    default ItemStack toStack(int amount) {
        return new ItemStack(get(), amount);
    }

    default ItemStack toStack() {
        return toStack(1);
    }

    default boolean isBlank() {
        return get() == Items.AIR;
    }
}
