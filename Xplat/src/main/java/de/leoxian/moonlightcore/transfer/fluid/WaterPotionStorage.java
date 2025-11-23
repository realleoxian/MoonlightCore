package de.leoxian.moonlightcore.transfer.fluid;

import de.leoxian.moonlightcore.transfer.ExtractionOnlyStorage;
import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.StoragePreconditions;
import de.leoxian.moonlightcore.transfer.context.ContainerItemContext;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;

public class WaterPotionStorage implements ExtractionOnlyStorage<FluidResource>, SingleSlotStorage<FluidResource> {
    private static final FluidResource CONTAINED_FLUID = FluidResource.of(Fluids.WATER);
    private static final int CONTAINED_AMOUNT = FluidConstants.BOTTLE;

    public static @Nullable WaterPotionStorage find(ContainerItemContext context) {
        return isWaterPotion(context) ? new WaterPotionStorage(context) : null;
    }

    private static boolean isWaterPotion(ContainerItemContext context) {
        ItemResource resource = context.getResource();
        return resource.isOf(Items.POTION) && PotionUtils.getPotion(resource.getNBT()) == Potions.WATER;
    }

    private final ContainerItemContext context;

    private WaterPotionStorage(ContainerItemContext context) {
        this.context = context;
    }

    @Override
    public int extract(TransactionContext context, FluidResource extractedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(extractedResource, maxAmount);

        if(!isWaterPotion()) return 0;

        if(extractedResource.equals(CONTAINED_FLUID) && maxAmount >= CONTAINED_AMOUNT) {
            if(this.context.exchange(context, mapToGlassBottle(), 1) == 1) {
                return CONTAINED_AMOUNT;
            }
        }

        return 0;
    }

    @Override
    public FluidResource getResource() {
        if (isWaterPotion()) {
            return CONTAINED_FLUID;
        }

        return FluidResource. blank();
    }

    @Override
    public int getAmount() {
        if(isWaterPotion()) {
            return CONTAINED_AMOUNT;
        }

        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return getResource().isBlank();
    }

    @Override
    public int getCapacity(FluidResource resource) {
        return getAmount();
    }

    @Override
    public String toString() {
        return "WaterPotionStorage[" + this.context + "]";
    }

    private boolean isWaterPotion() {
        return isWaterPotion(this.context);
    }

    private ItemResource mapToGlassBottle() {
        ItemStack newStack = context.getResource().toStack();
        PotionUtils.setPotion(newStack, Potions.EMPTY);

        return ItemResource.of(Items.GLASS_BOTTLE, newStack.getTag());
    }
}
