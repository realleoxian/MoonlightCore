package de.realleoxian.moonlightcore.mixin;

import de.realleoxian.moonlightcore.impl.transfer.item.SpecialLogicInventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleContainer.class)
public class SimpleContainerMixin implements SpecialLogicInventory {
    @Unique private boolean moonlightcore$suppressSpecialLogic = false;

    @Inject(
            method = "setItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/SimpleContainer;setChanged()V"
            ),
            cancellable = true
    )
    public void moonlightcore$redirectSetChanged(int index, ItemStack stack, CallbackInfo ci) {
        if (moonlightcore$suppressSpecialLogic) {
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
}
