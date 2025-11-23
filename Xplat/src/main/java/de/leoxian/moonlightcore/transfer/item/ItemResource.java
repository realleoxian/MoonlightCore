package de.leoxian.moonlightcore.transfer.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.transfer.TransferResource;
import de.leoxian.moonlightcore.util.ByteBufCodecs;
import de.leoxian.moonlightcore.util.StreamCodec;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;
import java.util.Optional;

public class ItemResource implements TransferResource<Item> {
    private static final ItemResource BLANK = new ItemResource(Items.AIR, null);

    public static final Codec<ItemResource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(resource -> resource.item),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(resource -> Optional.ofNullable(resource.tag))
    ).apply(instance, (item, optTag) -> new ItemResource(item, optTag.orElse(null))));

    public static final StreamCodec<ByteBuf, ItemResource> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static ItemResource blank() {
        return BLANK;
    }

    public static ItemResource of(ItemStack stack) {
        return of(stack.getItem(), stack.getTag());
    }

    public static ItemResource of(ItemLike itemLike) {
        return of(itemLike, null);
    }

    public static ItemResource of(ItemLike itemLike, @Nullable CompoundTag tag) {
        return of(itemLike.asItem(), tag);
    }

    public static ItemResource of(Item item, @Nullable CompoundTag tag) {
        Objects.requireNonNull(item, "Item may not be null");

        if(tag == null || item == Items.AIR) {
            return ((ItemResourceCache) item).mlcore_getCachedItemResource();
        } else {
            return new ItemResource(item, tag);
        }
    }

    public static ItemResource fromNBT(CompoundTag nbt) {
        Objects.requireNonNull(nbt, "NBT may not be null");

        try {
            Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(nbt.getString("item")));
            @Nullable CompoundTag tag = nbt.contains("tag") ? nbt.getCompound("tag") : null;

            return of(item, tag);
        } catch (Exception exception) {
            MoonlightCore.LOGGER.debug("Tried to load an invalid ItemResource from NBT: {}", nbt, exception);
            return ItemResource.blank();
        }
    }

    private final Item item;
    private @Nullable final CompoundTag tag;
    private final int hashCode;


    private volatile @Nullable ItemStack cachedStack = null;

    public ItemResource(Item item, @Nullable CompoundTag tag) {
        this.item = item;
        this.tag = tag == null ? null : tag.copy();
        this.hashCode = Objects.hash(item, tag);
    }

    public ItemStack getCachedStack() {
        ItemStack stack = this.cachedStack;

        if(stack == null) {
            cachedStack = stack = this.toStack();
        }

        return stack;
    }

    public ItemStack toStack(int count) {
        if(this.isBlank()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(getResource(), count);
        stack.setTag(copyNBT());
        return stack;
    }

    public boolean matches(ItemStack stack) {
        return isOf(stack.getItem()) && nbtMatches(stack.getTag());
    }

    public ItemStack toStack() {
        return toStack(1);
    }

    public Holder<Item> getRegistryHolder() {
        return getResource().builtInRegistryHolder();
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag result = new CompoundTag();
        result.putString("item", BuiltInRegistries.ITEM.getKey(this.item).toString());

        if(this.tag != null) {
            result.put("tag", this.tag.copy());
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;

        ItemResource other = (ItemResource) obj;
        return this.hashCode == other.hashCode && item == other.item && nbtMatches(other.tag);
    }

    @Override
    public String toString() {
        return "ItemTransferResource[item=" + this.item + ", tag=" + this.tag + "]";
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public Item getResource() {
        return this.item;
    }

    @Override
    public boolean isBlank() {
        return this.item == Items.AIR;
    }

    @Override
    public @Nullable CompoundTag getNBT() {
        return this.tag;
    }
}
