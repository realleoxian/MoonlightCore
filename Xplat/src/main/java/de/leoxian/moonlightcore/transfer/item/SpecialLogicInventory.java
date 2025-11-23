package de.leoxian.moonlightcore.transfer.item;

import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

public interface SpecialLogicInventory {

    void mlcore_setSuppress(boolean suppress);

    void mlcore_onFinalCommit(int slot, ItemStack oldStack, ItemStack newStack);

    default void mlcore_onTransfer(TransactionContext context, int slot) {}

}
