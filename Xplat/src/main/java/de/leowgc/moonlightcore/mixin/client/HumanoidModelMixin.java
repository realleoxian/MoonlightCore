package de.leowgc.moonlightcore.mixin.client;

import de.leowgc.moonlightcore.api.client.animation.ItemAnimation;
import de.leowgc.moonlightcore.api.client.animation.ItemWithAnimation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> {

    @SuppressWarnings("unchecked")
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("RETURN"))
    public void mlcore_setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if(!(entity instanceof Player player)) return;
        HumanoidModel<T> self = (HumanoidModel<T>) (Object) this;

        for(InteractionHand hand : InteractionHand.values()) {
            ItemStack itemInHand = player.getItemInHand(hand);
            if(itemInHand.isEmpty()) continue;

            Item item = itemInHand.getItem();
            if(!(item instanceof ItemWithAnimation animationItem)) continue;

            ItemAnimation animation = animationItem.getAnimation(itemInHand);
            if(animation == null) continue;

            if(animation.isItemBeingUsed(player)) {
                animation.setupUseAnimation(hand, itemInHand, self, player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            } else {
                animation.setupAnimation(hand, itemInHand, self, player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            }
        }
    }

}
