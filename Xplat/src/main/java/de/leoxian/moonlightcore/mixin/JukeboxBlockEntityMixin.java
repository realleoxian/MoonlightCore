package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.transfer.SpecialLogicInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.ticks.ContainerSingleItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityMixin implements SpecialLogicInventory, ContainerSingleItem {
    @Shadow
    public abstract void setItem(int slot, ItemStack stack);

    @Shadow
    @Final
    private NonNullList<ItemStack> items;
    @Unique
    private boolean mlcore_suppressSpecialLogic = false;

    @Inject(method = "setItem", at = @At("HEAD"), cancellable = true)
    private void mlcore_setStackBypass(int slot, ItemStack stack, CallbackInfo ci) {
        if(this.mlcore_suppressSpecialLogic) {
            this.items.set(0, stack);
            ci.cancel();
        }
    }

    @Override
    public void mlcore_onFinalCommit(int slot, ItemStack oldStack, ItemStack newStack) {
        setItem(slot, newStack);
    }

    @Override
    public void mlcore_setSuppress(boolean suppress) {
        this.mlcore_suppressSpecialLogic = suppress;
    }
}
