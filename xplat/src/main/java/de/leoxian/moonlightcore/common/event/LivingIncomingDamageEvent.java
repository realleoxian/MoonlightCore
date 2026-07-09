package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import de.leoxian.moonlightcore.common.event.base.EventResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingIncomingDamageEvent {
    Event<LivingIncomingDamageEvent> EVENT = Event.create(LivingIncomingDamageEvent.class, listeners -> (livingEntity, damageSource, damageAmount) -> {
       var result = EventResult.TRUE;
       for (final var listener : listeners) {
           result = listener.onLivingIncomingDamage(livingEntity, damageSource, damageAmount);
           if (result.cancelFurtherEventProcessing()) {
               break;
           }
       }
       return result;
    });

    EventResult onLivingIncomingDamage(LivingEntity livingEntity, DamageSource damageSource, float damageAmount);
}
