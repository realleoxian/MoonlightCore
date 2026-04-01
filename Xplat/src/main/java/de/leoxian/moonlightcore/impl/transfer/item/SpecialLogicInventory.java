package de.leoxian.moonlightcore.impl.transfer.item;

import de.leoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

public interface SpecialLogicInventory {

    void moonlightcore$setSupress(boolean suppress);

    void moonlightcore$onRootCommit(int slot, ItemStack oldStack, ItemStack newStack);

    default void moonlightcore$onTransfer(TransactionContext tx, int slot) {

    }

}
