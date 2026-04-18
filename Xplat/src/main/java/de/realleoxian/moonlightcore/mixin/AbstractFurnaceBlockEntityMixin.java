package de.realleoxian.moonlightcore.mixin;

import de.realleoxian.moonlightcore.impl.transfer.item.SpecialLogicInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin extends BaseContainerBlockEntity implements SpecialLogicInventory {

    @Shadow
    int cookingTotalTime;

    @Shadow
    int cookingProgress;
    @Shadow
    protected NonNullList<ItemStack> items;

    @Shadow
    private static int getTotalCookTime(Level level, AbstractFurnaceBlockEntity blockEntity) {
        throw new AssertionError();
    }

    @Unique private boolean moonlightcore$suppressSpecialLogic = false;

    protected AbstractFurnaceBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(
            method = "setItem",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void moonlightcore$SetStackSuppressUpdate(int index, ItemStack stack, CallbackInfo ci) {
        if (moonlightcore$suppressSpecialLogic) {
            items.set(index, stack);
            ci.cancel();
        }
    }

    @Override
    public void moonlightcore$setSupress(boolean suppress) {
        moonlightcore$suppressSpecialLogic = suppress;
    }

    @Override
    public void moonlightcore$onRootCommit(int slot, ItemStack oldStack, ItemStack newStack) {
        if (slot == 0) {
            boolean bl = !newStack.isEmpty() && ItemStack.isSameItemSameTags(newStack, oldStack);

            if (!bl && level instanceof ServerLevel level) {
                cookingTotalTime = getTotalCookTime(level, (AbstractFurnaceBlockEntity) (Object) this);
                cookingProgress = 0;
            }
        }
    }
}
