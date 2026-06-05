package de.realleoxian.moonlightcore.api.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class LivingDeathEvent extends EventBase implements CancellableEvent {
    public static final Event<LivingDeathEvent> EVENT = Event.create(LivingDeathEvent.class);

    public final LivingEntity entity;
    public final DamageSource source;

    public LivingDeathEvent(LivingEntity entity, DamageSource source) {
        this.entity = entity;
        this.source = source;
    }
}
