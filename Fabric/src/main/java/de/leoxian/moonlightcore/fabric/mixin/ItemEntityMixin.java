package de.leoxian.moonlightcore.fabric.mixin;

import de.leoxian.moonlightcore.event.common.PlayerEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow
    public abstract ItemStack getItem();

    @Unique
    private ItemStack mlcore_cache = null;

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I"), cancellable = true)
    private void mlcore_prePickup(Player player, CallbackInfo ci) {
        this.mlcore_cache = getItem().copy();

        if(PlayerEvent.ITEM_PICKUP_VALIDATION.invoker().canPickupItem(player, (ItemEntity) (Object) this, getItem()).isFalse()) {
            ci.cancel();
        }
    }

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;take(Lnet/minecraft/world/entity/Entity;I)V"))
    private void mlcore_pickup(Player player, CallbackInfo ci) {
        if(this.mlcore_cache != null) {
            PlayerEvent.PICKUP_ITEM.invoker().onItemPickup(player, (ItemEntity) (Object) this, this.mlcore_cache);
        }

        this.mlcore_cache = null;
    }
}
