package de.leowgc.moonlightcore.api.client.animation;

import net.minecraft.world.item.ItemStack;

public interface ItemWithAnimation {

    ItemAnimation getAnimation(ItemStack stack);

}
