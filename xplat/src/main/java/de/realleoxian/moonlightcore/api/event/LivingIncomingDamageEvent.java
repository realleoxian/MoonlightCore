package de.realleoxian.moonlightcore.api.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class LivingIncomingDamageEvent extends EventBase implements CancellableEvent {
    public static final Event<LivingIncomingDamageEvent> EVENT = Event.create(LivingIncomingDamageEvent.class);

    public final LivingEntity livingEntity;
    public final DamageSource damageSource;
    public final float originalDamageAmount;
    public float damageAmount;

    public LivingIncomingDamageEvent(LivingEntity livingEntity, DamageSource damageSource, float originalDamageAmount) {
        this.livingEntity = livingEntity;
        this.damageSource = damageSource;
        this.originalDamageAmount = originalDamageAmount;
        this.damageAmount = originalDamageAmount;
    }
}
