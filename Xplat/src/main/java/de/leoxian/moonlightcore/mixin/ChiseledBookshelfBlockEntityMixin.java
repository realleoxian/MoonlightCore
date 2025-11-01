package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.transfer.SpecialLogicInventory;
import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
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
public class ChiseledBookshelfBlockEntityMixin implements SpecialLogicInventory {
    @Shadow @Final
    private NonNullList<ItemStack> items;
    @Shadow
    private int lastInteractedSlot;

    @Unique
    private boolean mlcore_suppressSpecialLogic = false;

    @Unique
    private final SnapshotJournal<Integer> mlcore_lastInteractedParticipan = new SnapshotJournal<Integer>() {
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

    @Shadow
    private void updateState(int interactedSlot) {
        throw new AssertionError();
    }

    @Inject(method = "setItem", at = @At("HEAD"), cancellable = true)
    private void mlcore_setStackBypass(int slot, ItemStack stack, CallbackInfo ci) {
        if(this.mlcore_suppressSpecialLogic) {
            this.items.set(slot, stack);
            ci.cancel();
        }
    }

    @Override
    public void mlcore_onTransfer(TransactionContext ctx, int slot) {
        mlcore_lastInteractedParticipan.updateSnapshots(ctx);
        lastInteractedSlot = slot;
    }

    @Override
    public void mlcore_onFinalCommit(int slot, ItemStack oldStack, ItemStack newStack) {}

    @Override
    public void mlcore_setSuppress(boolean suppress) {
        this.mlcore_suppressSpecialLogic = suppress;
    }
}
