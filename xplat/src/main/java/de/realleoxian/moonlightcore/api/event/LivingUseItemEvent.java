package de.realleoxian.moonlightcore.api.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

public sealed class LivingUseItemEvent extends EventBase {
    public final LivingEntity entity;
    public final ItemStack itemStack;
    public final InteractionHand usedHand;
    public int duration;

    protected LivingUseItemEvent(LivingEntity entity, ItemStack itemStack, InteractionHand usedHand, int duration) {
        this.entity = entity;
        this.itemStack = itemStack;
        this.usedHand = usedHand;
        this.duration = duration;
    }

    public LivingUseItemEvent(LivingEntity entity, ItemStack itemStack, int duration) {
        this(entity, itemStack, entity.getUsedItemHand(), duration);
    }

    public static final class Start extends LivingUseItemEvent implements CancellableEvent {
        @ApiStatus.Internal
        public Start(LivingEntity entity, ItemStack itemStack, InteractionHand usedHand, int duration) {
            super(entity, itemStack, usedHand, duration);
        }
    }

    public static final class Tick extends LivingUseItemEvent implements CancellableEvent {
        @ApiStatus.Internal
        public Tick(LivingEntity entity, ItemStack itemStack, int duration) {
            super(entity, itemStack, duration);
        }
    }

    public static final class Stop extends LivingUseItemEvent implements CancellableEvent {
        @ApiStatus.Internal
        public Stop(LivingEntity entity, ItemStack itemStack, int duration) {
            super(entity, itemStack, duration);
        }
    }

    public static final class Finish extends LivingUseItemEvent {
        public ItemStack result;

        public Finish(LivingEntity entity, ItemStack itemStack, int duration, ItemStack result) {
            super(entity, itemStack, duration);
            this.result = result;
        }
    }
}
