package de.leowgc.moonlightcore.api.client.animation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;

public interface ItemAnimation {

    default void onItemUse(HumanoidModel<?> model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    default void onItemUsedInRightHand(HumanoidModel<?> model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.onItemUse(model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    default void onItemUsedInLeftHand(HumanoidModel<?> model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.onItemUse(model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }


    default void onLeftHandItem(HumanoidModel<?> model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    default void onRightHandItem(HumanoidModel<?> model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

}
