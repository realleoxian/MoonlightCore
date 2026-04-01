package de.leoxian.moonlightcore.impl.transfer.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.leoxian.moonlightcore.api.transfer.item.ItemResource;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public class ItemResourceImpl implements ItemResource {
    private static final Logger LOGGER = LoggerFactory.getLogger("moonlightcore-transfer-api/item");
    private static final ItemResource BLANK = new ItemResourceImpl(Items.AIR, null);

    public static final Codec<ItemResource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("item", Items.AIR).forGetter(resource -> ((ItemResourceImpl) resource).item),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(resource -> Optional.ofNullable(((ItemResourceImpl) resource).tag))
    ).apply(instance, (item, tag) -> of(item, tag.orElse(null))));

    private static final String TAG_ITEM = "item";
    private static final String TAG_TAG = "tag";

    public static ItemResource blank() {
        return BLANK;
    }

    public static ItemResource of(Item item, @Nullable CompoundTag tag) {
        Objects.requireNonNull(item, "Item may not be 'null'");

        if(item == Items.AIR || tag == null) {
            return ((ItemResourceCache) item).moonlightcore$getCachedItemResource();
        }

        return new ItemResourceImpl(item, tag);
    }

    public static ItemResource fromBuffer(FriendlyByteBuf byteBuf) {
        Objects.requireNonNull(byteBuf, "Buffer may not be 'null'");

        Item item = Item.byId(byteBuf.readVarInt());
        CompoundTag tag = byteBuf.readNbt();
        return of(item, tag);
    }

    private final Item item;
    private final @Nullable CompoundTag tag;
    private final int hashCode;

    public ItemResourceImpl(Item item, @Nullable CompoundTag tag) {
        this.item = item;
        this.tag = tag == null ? null : tag.copy();
        this.hashCode = Objects.hash(item, tag);
    }

    @Override
    public Item get() {
        return item;
    }

    @Override
    public @Nullable CompoundTag getTag() {
        return tag;
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf byteBuf) {
        if(isBlank()) {
            byteBuf.writeBoolean(false);
            return;
        }

        byteBuf.writeBoolean(true);
        byteBuf.writeVarInt(Item.getId(item));
        byteBuf.writeNbt(tag);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj.getClass() != getClass()) return false;

        ItemResourceImpl resource = (ItemResourceImpl) obj;
        return resource.item == item && resource.tag == tag;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "ItemResource[item=%s, tag=%s]".formatted(BuiltInRegistries.ITEM.getKey(item), tag);
    }
}
