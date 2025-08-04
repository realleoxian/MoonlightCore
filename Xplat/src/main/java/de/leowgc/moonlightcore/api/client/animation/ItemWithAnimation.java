package de.leowgc.moonlightcore.api.client.animation;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ItemWithAnimation {

    ItemAnimation getAnimation(ItemStack stack);

    default boolean isRightHand(Player player, InteractionHand hand) {
        if(player.getMainArm() == HumanoidArm.LEFT) {
            return hand != InteractionHand.MAIN_HAND;
        }

        return hand == InteractionHand.MAIN_HAND;
    }

    default boolean isSameHand(HumanoidArm mainArm, HumanoidArm arm, InteractionHand hand) {
        if(mainArm == arm) {
            return hand == InteractionHand.MAIN_HAND;
        }

        return hand == InteractionHand.OFF_HAND;
    }

    default boolean isItemBeingUsed(LivingEntity player) {
        return (player.isUsingItem() && player.getUseItemRemainingTicks() > 0);
    }
}
