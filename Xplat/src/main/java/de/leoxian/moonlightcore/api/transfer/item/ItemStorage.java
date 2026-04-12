package de.leoxian.moonlightcore.api.transfer.item;

import de.leoxian.moonlightcore.api.apilookup.block.BlockApiLookup;
import de.leoxian.moonlightcore.api.transfer.SidedStorageBlockEntity;
import de.leoxian.moonlightcore.api.transfer.storage.Storage;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public final class ItemStorage {
    public static final BlockApiLookup<Storage<ItemResource>, @Nullable Direction> SIDED = BlockApiLookup.find(new ResourceLocation("moonlightcore", "sided_item_storage"), Storage.asClass(), Direction.class);

    static {
        SIDED.registerFallback((level, blockPos, blockState, blockEntity, context) -> {
            if(blockEntity instanceof SidedStorageBlockEntity) {
                return ((SidedStorageBlockEntity) blockEntity).getItemStorage(context);
            }

            return null;
        });
    }

    private ItemStorage() {}
}
