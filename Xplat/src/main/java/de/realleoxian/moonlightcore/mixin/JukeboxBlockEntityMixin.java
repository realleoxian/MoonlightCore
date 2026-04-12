package de.realleoxian.moonlightcore.mixin;

import de.realleoxian.moonlightcore.impl.transfer.item.SpecialLogicInventory;
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
    @Final
    private NonNullList<ItemStack> items;
    @Unique
    private boolean moonlightcore$suppressSpecialLogic = false;

    @Inject(
            method = "setItem",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void moonlightcore$setStackBypass(int slot, ItemStack stack, CallbackInfo ci) {
        if (moonlightcore$suppressSpecialLogic) {
            items.set(0, stack);
            ci.cancel();
        }
    }

    @Override
    public void moonlightcore$setSupress(boolean suppress) {
        moonlightcore$suppressSpecialLogic = suppress;
    }

    @Override
    public void moonlightcore$onRootCommit(int slot, ItemStack oldStack, ItemStack newStack) {
        setItem(slot, newStack);
    }

}
