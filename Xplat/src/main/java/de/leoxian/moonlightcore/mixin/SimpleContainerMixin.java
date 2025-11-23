package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.transfer.item.SpecialLogicInventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleContainer.class)
public class SimpleContainerMixin implements SpecialLogicInventory {
    @Unique
    private boolean mlcore_suppressSpecialLogic = false;

    @Inject(method = "setItem(ILnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SimpleContainer;setChanged()V"), cancellable = true)
    public void mlcore_redirectSetChanged(int index, ItemStack stack, CallbackInfo ci) {
        if(this.mlcore_suppressSpecialLogic) {
            ci.cancel();
        }
    }

    @Override
    public void mlcore_onFinalCommit(int slot, ItemStack oldStack, ItemStack newStack) {}

    @Override
    public void mlcore_setSuppress(boolean suppress) {
        this.mlcore_suppressSpecialLogic = suppress;
    }
}
