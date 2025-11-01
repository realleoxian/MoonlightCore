package de.leoxian.moonlightcore.transfer.fluid;

import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.lookup.block.BlockApiLookup;
import de.leoxian.moonlightcore.lookup.item.ItemApiLookup;
import de.leoxian.moonlightcore.mixin.accessor.BucketItemAccessor;
import de.leoxian.moonlightcore.transfer.SidedStorageBlockEntity;
import de.leoxian.moonlightcore.transfer.Storage;
import de.leoxian.moonlightcore.transfer.context.CombinedProviders;
import de.leoxian.moonlightcore.transfer.context.ItemStorageContext;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.util.nullness.NullableType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class FluidStorage {
    public static final BlockApiLookup<Storage<Fluid, FluidResource>, @NullableType Direction> SIDED = BlockApiLookup.get(MoonlightCore.location("sided_fluid_storage"), Storage.asClass(), Direction.class);
    public static final ItemApiLookup<Storage<Fluid, FluidResource>, ItemStorageContext> ITEM = ItemApiLookup.get(MoonlightCore.location("fluid_storage"), Storage.asClass(), ItemStorageContext.class);

    public static final Event<CombinedItemApiProvider> GENERAL_COMBINED_PROVIDERS_EVENT = CombinedProviders.createEvent(false);

    static {
        CauldronFluidContent.getForFluid(Fluids.WATER);

        FluidStorage.SIDED.registerFallback((level, pos, state, be, direction) -> {
            if(be instanceof SidedStorageBlockEntity sidedStorageBlockEntity) {
                return sidedStorageBlockEntity.getFluidStorage(direction);
            }

            return null;
        });

        FluidStorage.ITEM.registerFallback((stack, ctx) -> GENERAL_COMBINED_PROVIDERS_EVENT.invoker().find(ctx));
        combinedItemApiProvider(Items.BUCKET).subscribe(EmptyBucketStorage::new);

        GENERAL_COMBINED_PROVIDERS_EVENT.subscribe((context) -> {
            if(context.resource().get() instanceof BucketItem bucketItem) {
                Fluid fluid = ((BucketItemAccessor) bucketItem).getContent();

                if(fluid != null && fluid.getBucket() == bucketItem) {
                    return new FullItemFluidStorage(context, Items.BUCKET, FluidResource.of(fluid), FluidConstants.BUCKET);
                }
            }

            return null;
        });

        combinedItemApiProvider(Items.GLASS_BOTTLE).subscribe(ctx ->
                new EmptyItemFluidStorage(ctx, emptyBottle -> {
                    ItemStack newStack = emptyBottle.toStack();
                    PotionUtils.setPotion(newStack, Potions.WATER);
                    return ItemResource.of(Items.POTION, newStack.getTag());
        }, Fluids.WATER, FluidConstants.BOTTLE));
        combinedItemApiProvider(Items.POTION).subscribe(WaterPotionStorage::find);
    }

    public static Event<CombinedItemApiProvider> combinedItemApiProvider(Item item) {
        return CombinedProviders.getOrCreateItemEvent(item);
    }

    @FunctionalInterface
    public interface CombinedItemApiProvider {
        @Nullable
        Storage<Fluid, FluidResource> find(ItemStorageContext context);
    }

    private FluidStorage() {}
}
