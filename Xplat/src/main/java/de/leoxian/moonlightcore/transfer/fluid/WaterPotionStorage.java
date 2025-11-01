package de.leoxian.moonlightcore.transfer.fluid;

import de.leoxian.moonlightcore.transfer.ExtractionOnlyStorage;
import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.StorageInternals;
import de.leoxian.moonlightcore.transfer.context.ItemStorageContext;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class WaterPotionStorage implements ExtractionOnlyStorage<FluidResource>, SingleSlotStorage<FluidResource> {
    private static final FluidResource CONTAINED_FLUID = FluidResource.of(Fluids.WATER);
    private static final int CONTAINED_AMOUNT = FluidConstants.BOTTLE;

    @Nullable
    public static WaterPotionStorage find(ItemStorageContext context) {
        return isWaterPotion(context) ? new WaterPotionStorage(context) : null;
    }

    private static boolean isWaterPotion(ItemStorageContext context) {
        ItemResource resource = context.resource();
        return resource.is(Items.POTION) && PotionUtils.getPotion(resource.getNBT()) == Potions.WATER;
    }

    private final ItemStorageContext context;

    private WaterPotionStorage(ItemStorageContext context) {
        this.context = context;
    }

    @Override
    public int extract(Transaction tx, FluidResource resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        if(!isWaterPotion()) {
            return 0;
        }

        if(resource.is(CONTAINED_FLUID.get()) && amount >= CONTAINED_AMOUNT) {
            if(context.exchange(tx, mapToGlassBottle(), 1) == 1) {
                return CONTAINED_AMOUNT;
            }
        }

        return 0;
    }

    @Override
    public boolean isResourceValid(FluidResource resource) {
        return resource.is(CONTAINED_FLUID.get());
    }

    @Override
    public FluidResource resource() {
        if(isWaterPotion()) {
            return CONTAINED_FLUID;
        }

        return FluidResource.empty();
    }

    @Override
    public int amount() {
        if (isWaterPotion()) {
            return CONTAINED_AMOUNT;
        }

        return 0;
    }

    @Override
    public int getCapacity(FluidResource resource) {
        return amount();
    }

    private ItemResource mapToGlassBottle() {
        ItemStack newStack = context.resource().toStack();
        PotionUtils.setPotion(newStack, Potions.EMPTY);

        return ItemResource.of(Items.GLASS_BOTTLE, newStack.getTag());
    }

    private boolean isWaterPotion() {
        return isWaterPotion(context);
    }
}
