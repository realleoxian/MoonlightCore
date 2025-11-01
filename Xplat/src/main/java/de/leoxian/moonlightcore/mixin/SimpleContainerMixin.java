package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.transfer.SpecialLogicInventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SimpleContainer.class)
public class SimpleContainerMixin implements SpecialLogicInventory {
    @Unique
    private boolean mlcore_suppressSpecialLogic = false;

    @Redirect(method = "setItem(ILnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SimpleContainer;setChanged()V"))
    public void mlcore_redirectSetChanged(SimpleContainer self) {
        if(!this.mlcore_suppressSpecialLogic) {
            self.setChanged();
        }
    }

    @Override
    public void mlcore_onFinalCommit(int slot, ItemStack oldStack, ItemStack newStack) {}

    @Override
    public void mlcore_setSuppress(boolean suppress) {
        this.mlcore_suppressSpecialLogic = suppress;
    }
}
