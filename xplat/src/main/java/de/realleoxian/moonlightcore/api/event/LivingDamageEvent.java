package de.realleoxian.moonlightcore.api.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class LivingDamageEvent {
    public static final Event<Pre> PRE = Event.create(Pre.class);
    public static final Event<Post> POST = Event.create(Post.class);

    private LivingDamageEvent() {}

    public static final class Pre extends EventBase {
        public final LivingEntity entity;
        public final DamageSource source;
        public final float originalDamage;
        public float damage;

        public Pre(LivingEntity entity, DamageSource source, float originalDamage) {
            this.entity = entity;
            this.source = source;
            this.originalDamage = originalDamage;
        }
    }

    public static final class Post extends EventBase {
        public final DamageSource source;
        public final float damage;

        public Post(DamageSource source, float damage) {
            this.source = source;
            this.damage = damage;
        }
    }
}
