package de.leoxian.moonlightcore.mixin.accessor;

import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EquipmentSlot.class)
public interface EquipmentSlotAccessor {
    @Accessor
    int getCountLimit();
}
