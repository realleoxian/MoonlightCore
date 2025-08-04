package de.leowgc.moonlightcore.api.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface ItemAnimation {

    default void setupAnimation(InteractionHand hand, ItemStack animatedItemStack, HumanoidModel<?> model, Player player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    default void setupUseAnimation(InteractionHand hand, ItemStack animatedItemStack, HumanoidModel<?> model, Player player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}


    default void renderArmWithItem(LivingEntity livingEntity, ItemStack animatedItemStack, ItemDisplayContext displayContext, HumanoidArm arm, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight) {}

    default void renderArmWithItem(AbstractClientPlayer player, InteractionHand hand, ItemStack stack, MultiBufferSource bufferSource, PoseStack poseStack, float pitch, float swingProgress, float equippedProgress, float partialTicks) {}


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
