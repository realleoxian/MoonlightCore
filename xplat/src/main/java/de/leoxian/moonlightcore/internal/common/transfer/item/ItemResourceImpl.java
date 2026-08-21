package de.leoxian.moonlightcore.internal.common.transfer.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.leoxian.moonlightcore.common.transfer.item.ItemResource;
import de.leoxian.moonlightcore.internal.common.transfer.StorageInternals;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;

public class ItemResourceImpl implements ItemResource {
    public static final ItemResource EMPTY = of(Items.AIR, DataComponentPatch.EMPTY);
    public static final Codec<ItemResource> CODEC = RecordCodecBuilder.create(i -> i.group(
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(ItemResource::typeHolder),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemResource::componentsPatch)
    ).apply(i, ItemResource::of));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemResource> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.ITEM), ItemResource::typeHolder,
            DataComponentPatch.STREAM_CODEC, ItemResource::componentsPatch,
            ItemResource::of
    );

    public static ItemResource of(Item item, DataComponentPatch componentPatch) {
        Objects.requireNonNull(item, "Item may not be 'null'");
        Objects.requireNonNull(componentPatch, "Component patch may not be 'null'");

        if (item == Items.AIR || componentPatch == DataComponentPatch.EMPTY) {
            return ((ItemResourceCache) item).moonlightcore$getCachedItemResource();
        }
        return new ItemResourceImpl(item, componentPatch);
    }

    public static int getMaxStackSize(ItemResource resource) {
        return resource.getOrDefault(DataComponents.MAX_STACK_SIZE, resource.item().getDefaultMaxStackSize());
    }

    private final Item item;
    private final DataComponentPatch componentPatch;
    private final int hashCode;
    private ItemStack cachedStack = null;

    public ItemResourceImpl(Item item, DataComponentPatch componentPatch) {
        this.item = item;
        this.componentPatch = componentPatch;
        this.hashCode = Objects.hash(item, componentPatch);
    }

    @Override
    public ItemResource applyPatch(DataComponentPatch patch) {
        return of(this.item, StorageInternals.mergePatches(this.componentPatch, patch));
    }

    @Override
    public DataComponentPatch componentsPatch() {
        return this.componentPatch;
    }

    @Override
    public Item item() {
        return this.item;
    }

    @Override
    public ItemStack toStack() {
        ItemStack ret = this.cachedStack;
        if (ret == null) {
            ret = cachedStack = new ItemStack(typeHolder(), 1, componentsPatch());
        }
        return ret.copy();
    }

    @Override
    public boolean isEmpty() {
        return this.item == Items.AIR;
    }

    @Override
    public Holder<Item> typeHolder() {
        return this.item.builtInRegistryHolder();
    }

    @Override
    public DataComponentMap getComponents() {
        return this.item.components();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj.getClass() != this.getClass()) return false;
        ItemResourceImpl other = (ItemResourceImpl) obj;
        return this.item == other.item &&
                this.componentPatch.equals(other.componentPatch) &&
                this.hashCode == other.hashCode;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return "ItemResource[item=" + this.item + ", components=" + this.componentPatch + "]";
    }
}
