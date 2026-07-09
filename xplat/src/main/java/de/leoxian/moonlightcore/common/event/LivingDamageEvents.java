package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class LivingDamageEvents {
    public static final Event<Pre> PRE = Event.create(Pre.class, listeners -> (entity, damageSource, damageAmount) -> {
       for (final var listener : listeners) {
           listener.onPreLivingDamage(entity, damageSource, damageAmount);
       }
    });
    public static final Event<Post> POST = Event.create(Post.class, listeners -> (entity, damageSource) -> {
        for (final var listener : listeners) {
            listener.onPostLivingDamage(entity, damageSource);
        }
    });

    private LivingDamageEvents() {}

    @FunctionalInterface
    public interface Pre {
        void onPreLivingDamage(LivingEntity entity, DamageSource damageSource, float damageAmount);
    }

    @FunctionalInterface
    public interface Post {
        void onPostLivingDamage(LivingEntity entity, DamageSource damageSource);
    }
}
