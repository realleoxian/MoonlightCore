package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import de.leoxian.moonlightcore.common.event.base.EventResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingDeathEvent {
    Event<LivingDeathEvent> EVENT = Event.create(LivingDeathEvent.class, listeners -> (entity, damageSource) -> {
       var result = EventResult.TRUE;
       for (final var listener : listeners) {
           result = listener.onLivingDeath(entity, damageSource);
           if (result.cancelFurtherEventProcessing()) {
               break;
           }
       }
       return result;
    });

    EventResult onLivingDeath(LivingEntity entity, DamageSource damageSource);
}
