package de.leoxian.moonlightcore.event.common;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.core.SectionPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface EntityEvent {
     /**
      * @see LivingDeath#onEntityDeath(LivingEntity, DamageSource)
      */
     Event<LivingDeath> LIVING_DEATH = EventFactory.createWithResult(LivingDeath.class);
     /**
      * @see LivingHurt#onEntityHurt(LivingEntity, DamageSource, float)
      */
     Event<LivingHurt> LIVING_HURT = EventFactory.createWithResult(LivingHurt.class);
     /**
      * @see Addition#onEntityAddition(Level, Entity)
      */
     Event<Addition> ADDITION = EventFactory.createWithResult(Addition.class);
     /**
      * @see EnterSection#onEnterSection(Entity, SectionPos, SectionPos)
      */
     Event<EnterSection> ENTER_SECTION = EventFactory.create(EnterSection.class);

     interface LivingDeath {
          /**
           * Invoked before an entity dies
           * @param entity The entity that is about to die
           * @param source The source of damage triggering to death
           */
          Event.Result onEntityDeath(LivingEntity entity, DamageSource source);
     }

     interface LivingHurt {
          /**
           * Invoked before an entity is hurt by a damage source
           * @param entity The entity that is attacked
           * @param source The reason why the entity takes damage
           * @param amount The amount of damage the entity takes
           * @return A {@link Event.Result} determining the outcome of the event, the execution of the entity attack may be cancelled by the result
           */
          Event.Result onEntityHurt(LivingEntity entity, DamageSource source, float amount);
     }

     interface Addition {
          /**
           * Invoked when an entity is about to be added to the world
           * @param level The level the entity is added to
           * @param entity The entity to add the level
           * @return A {@link Event.Result} determining the outcome of the event, the execution of the entity addition may be cancelled by the result
           */
          Event.Result onEntityAddition(Level level, Entity entity);
     }

     interface EnterSection {
          /**
           * Invoked whenever an entity enters a chunk
           * @param entity The entity moving to a different chunk
           * @param current The current chunk where is the entity
           * @param previous The previous chunk where was the entity
           */
          void onEnterSection(Entity entity, SectionPos current, SectionPos previous);
     }
}
