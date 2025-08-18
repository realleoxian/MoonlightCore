package de.leowgc.moonlightcore.api.transfer.item;

import de.leowgc.moonlightcore.api.transfer.TransferResource;
import de.leowgc.moonlightcore.transfer.item.ItemResourceImpl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface ItemResource extends TransferResource<ItemStack>  {

    static ItemResource of(ItemStack stack, int amount) {
        return new ItemResourceImpl(stack, amount);
    }

    static ItemResource of(ItemStack stack) {
        return of(stack, stack.getCount());
    }

    static ItemResource of(Item item) {
        return of(new ItemStack(item));
    }

    static ItemResource empty() {
        return of(ItemStack.EMPTY);
    }

}
