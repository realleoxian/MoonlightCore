package de.leoxian.moonlightcore.api.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class LivingEntityEvents {
    /**
     * @see Hurt#onEntityHurt(LivingEntity, DamageSource, float)
     */
    public static final EventBus<Hurt> HURT = EventBus.create((listeners) -> (entity, source, amount) -> {
        for(Hurt listener : listeners) {
            EventResult result = listener.onEntityHurt(entity, source, amount);

            if(result.cancelFurtherProcessing) {
                return result;
            }
        }

        return EventResult.TRUE;
    });
    /**
     * @see Death#onEntityDeath(LivingEntity, DamageSource)
     */
    public static final EventBus<Death> DEATH = EventBus.create((listeners) -> (entity, source) -> {
        for(Death listener : listeners) {
            EventResult result = listener.onEntityDeath(entity, source);

            if(result.cancelFurtherProcessing) {
                return result;
            }
        }

        return EventResult.TRUE;
    });
    /**
     * @see Attack#onEntityAttack(LivingEntity, DamageSource, float)
     */
    public static final EventBus<Attack> ATTACK = EventBus.create((listeners) -> (entity, source, amount) -> {
        for(Attack listener : listeners) {
            EventResult result = listener.onEntityAttack(entity, source, amount);

            if(result.cancelFurtherProcessing) {
                return result;
            }
        }

        return EventResult.TRUE;
    });

    private LivingEntityEvents() {}

    public interface Hurt {
        /**
         * Invoked whenever an entity gets hurt
         * @param entity    The entity that may get hurt
         * @param source    The source of damage that may hurt the entity
         * @param amount    The amount of damage the entity may get
         * @return An {@link EventResult} that may cancel the event, if cancelled, no hurt will be infringed
         */
        EventResult onEntityHurt(LivingEntity entity, DamageSource source, float amount);
    }

    public interface Death {
        /**
         * Invoked whenever an entity its killed
         * @param entity    The entity that was killed
         * @param source    The damage source that killed the entity
         * @return An {@link EventResult} that may cancel the event, if cancelled, the death will not be processed
         */
        EventResult onEntityDeath(LivingEntity entity, DamageSource source);
    }

    public interface Attack {
        /**
         * Invoked when an entity attacks other
         * @param entity    The entity that was attacked
         * @param source    The source of damage from the attack
         * @param amount    The amount of damage
         * @return An {@link EventResult} that may cancel the event, if cancelled, no attack will be infringed
         */
        EventResult onEntityAttack(LivingEntity entity, DamageSource source, float amount);
    }
}
