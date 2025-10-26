package de.leoxian.moonlightcore.fabric.mixin;

import de.leoxian.moonlightcore.event.common.BlockEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void mlcore_place(BlockPlaceContext _0, CallbackInfoReturnable<InteractionResult> cir, BlockPlaceContext blockPlaceContext, BlockState placedState) {
        if(BlockEvent.PLACE.invoker().onBlockPlace(blockPlaceContext.getLevel(), blockPlaceContext.getPlayer(), placedState, blockPlaceContext.getClickedPos()).isFalse()) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

}
