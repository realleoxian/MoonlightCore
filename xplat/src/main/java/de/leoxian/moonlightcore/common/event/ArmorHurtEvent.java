package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import de.leoxian.moonlightcore.common.event.base.EventResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

public interface ArmorHurtEvent {
    Event<ArmorHurtEvent> EVENT = Event.create(ArmorHurtEvent.class, listeners -> (livingEntity, damageSource, context) -> {
       var result = EventResult.TRUE;
       for (final var listener : listeners) {
           result = listener.onAmorHurt(livingEntity, damageSource, context);
           if (result.cancelFurtherEventProcessing()) {
               break;
           }
       }
       return result;
    });

    EventResult onAmorHurt(LivingEntity livingEntity, DamageSource damageSource, Context context);

    @ApiStatus.NonExtendable
    interface Context {
        ItemStack getArmorItemStack(EquipmentSlot slot);

        float getOriginalDamage(EquipmentSlot slot);

        float getDamage(EquipmentSlot slot);

        void setDamage(EquipmentSlot slot, float damage);
    }
}
