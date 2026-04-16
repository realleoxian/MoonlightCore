package de.realleoxian.moonlightcore.api.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class LivingEntityEvents {
    public static final EventBus<Hurt> HURT = EventBus.create(Hurt.class, (listeners) -> (entity, source, amount) -> {
        for(Hurt listener : listeners) {
            EventResult result = listener.onEntityHurt(entity, source, amount);

            if(result.cancelFurtherProcessing) {
                return result;
            }
        }

        return EventResult.TRUE;
    });
    public static final EventBus<Death> DEATH = EventBus.create(Death.class, (listeners) -> (entity, source) -> {
        for(Death listener : listeners) {
            EventResult result = listener.onEntityDeath(entity, source);

            if(result.cancelFurtherProcessing) {
                return result;
            }
        }

        return EventResult.TRUE;
    });
    public static final EventBus<Attack> ATTACK = EventBus.create(Attack.class, (listeners) -> (source, amount) -> {
        for(Attack listener : listeners) {
            EventResult result = listener.onEntityAttack(source, amount);

            if(result.cancelFurtherProcessing) {
                return result;
            }
        }

        return EventResult.TRUE;
    });

    private LivingEntityEvents() {}

    public interface Hurt {
        EventResult onEntityHurt(LivingEntity entity, DamageSource source, float amount);
    }

    public interface Death {
        EventResult onEntityDeath(LivingEntity entity, DamageSource source);
    }

    public interface Attack {
        EventResult onEntityAttack(DamageSource source, float amount);
    }
}
