package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.transfer.item.SpecialLogicInventory;
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
    protected NonNullList<ItemStack> items;
    @Shadow
    int cookingTotalTime;
    @Shadow
    int cookingProgress;

    @Shadow
    private static int getTotalCookTime(Level level, AbstractFurnaceBlockEntity blockEntity) {
        throw new AssertionError();
    }

    @Unique
    private boolean mlcore_suppressSpecialLogic = false;

    protected AbstractFurnaceBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "setItem", at = @At("HEAD"), cancellable = true)
    public void mlcore_setStackSupressUpdate(int index, ItemStack stack, CallbackInfo ci) {
        if(this.mlcore_suppressSpecialLogic) {
            this.items.set(index, stack);
            ci.cancel();
        }
    }

    @Override
    public void mlcore_onFinalCommit(int slot, ItemStack oldStack, ItemStack newStack) {
        if(slot == 0) {

            boolean bl = !newStack.isEmpty() && ItemStack.isSameItemSameTags(newStack, oldStack);
            if(!bl && this.level instanceof ServerLevel level) {
                this.cookingTotalTime = getTotalCookTime(level, (AbstractFurnaceBlockEntity) (Object) this);
                this.cookingProgress = 0;
            }
        }
    }

    @Override
    public void mlcore_setSuppress(boolean suppress) {
        this.mlcore_suppressSpecialLogic = suppress;
    }
}
