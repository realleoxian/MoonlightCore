package de.leoxian.moonlightcore.transfer.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.leoxian.moonlightcore.transfer.TransferResource;
import de.leoxian.moonlightcore.transfer.TransferResourceExtension;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public final class ItemResource implements TransferResource<Item> {
    private static final ItemResource EMPTY = of(Items.AIR);

    public static final Codec<ItemResource> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemResource::get),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter((resource) -> Optional.ofNullable(resource.getNBT()))
    ).apply(instance, (item, tag) -> of(item, tag.orElse(null))));


    public static ItemResource empty() {
        return EMPTY;
    }

    public static ItemResource of(ItemStack stack) {
        return of(stack.getItem(), stack.getTag());
    }

    public static ItemResource of(ItemLike like) {
        return of(like, null);
    }

    public static ItemResource of(ItemLike like, @Nullable CompoundTag nbt) {
        return of(like.asItem(), nbt);
    }

    @SuppressWarnings("unchecked")
    public static ItemResource of(Item item, @Nullable CompoundTag nbt) {
        Objects.requireNonNull(item, "Item cannot be null");

        if(nbt == null || item == Items.AIR) {
            return ((TransferResourceExtension<Item, ItemResource>) item).mlcore_getCachedResource();
        }

        return new ItemResource(item, nbt);
    }

    private final Item item;
    @Nullable
    private final CompoundTag tag;

    @Nullable
    private volatile ItemStack cachedStack = null;

    public ItemResource(Item item, @Nullable CompoundTag tag) {
        this.item = item;
        this.tag = tag == null ? null : tag.copy();
    }

    public ItemStack toStack() {
        return this.toStack(1);
    }

    public ItemStack toStack(int count) {
        return new ItemStack(this::get, count);
    }

    public ItemStack getCachedStack() {
        ItemStack ret = cachedStack;

        if(ret == null) {
            cachedStack = ret = toStack();
        }

        return ret;
    }

    @Override
    public Item get() {
        return this.item;
    }

    @Override
    public boolean isEmpty() {
        return this.get() == Items.AIR;
    }

    @Override
    public @Nullable CompoundTag getNBT() {
        return this.tag;
    }

    @Override
    public String toString() {
        return this.get().toString();
    }
}
