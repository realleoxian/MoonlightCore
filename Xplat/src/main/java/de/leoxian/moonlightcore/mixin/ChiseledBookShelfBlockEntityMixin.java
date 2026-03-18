package de.leoxian.moonlightcore.mixin;

import ca.weblite.objc.annotations.Msg;
import de.leoxian.moonlightcore.api.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.impl.transfer.item.SpecialLogicInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChiseledBookShelfBlockEntity.class)
public abstract class ChiseledBookShelfBlockEntityMixin implements SpecialLogicInventory {
    @Shadow
    @Final
    private NonNullList<ItemStack> items;
    @Shadow
    private int lastInteractedSlot;

    @Shadow
    protected abstract void updateState(int slot);

    @Unique
    private boolean moonlightcore$suppressSpecialLogic = false;

    @Unique
    private final SnapshotJournal<Integer> moonlightcore$lastInteractedParticipant = new SnapshotJournal<Integer>() {
        @Override
        public Integer createSnapshot() {
            return lastInteractedSlot;
        }

        @Override
        public void revertToSnapshot(Integer snapshot) {
            lastInteractedSlot = snapshot;
        }

        @Override
        public void onRootCommit(Integer originalState) {
            updateState(originalState);
        }
    };

    @Inject(
            method = "setItem",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void moonlightcore$setStackBypass(int slot, ItemStack stack, CallbackInfo ci) {
        if (moonlightcore$suppressSpecialLogic) {
            items.set(slot, stack);
            ci.cancel();
        }
    }

    @Override
    public void moonlightcore$setSupress(boolean suppress) {
        moonlightcore$suppressSpecialLogic = suppress;
    }

    @Override
    public void moonlightcore$onRootCommit(int slot, ItemStack oldStack, ItemStack newStack) {

    }

    @Override
    public void moonlightcore$onTransfer(TransactionContext tx, int slot) {
        moonlightcore$lastInteractedParticipant.updateSnapshots(tx);
        lastInteractedSlot = slot;
    }
}
